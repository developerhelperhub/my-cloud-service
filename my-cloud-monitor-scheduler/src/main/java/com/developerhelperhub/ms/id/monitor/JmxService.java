package com.developerhelperhub.ms.id.monitor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.monitor.application.ApplicationEntity;
import com.developerhelperhub.ms.id.monitor.application.InstanceEntity;
import com.developerhelperhub.ms.id.monitor.model.DiscoveryResponseModel;
import com.developerhelperhub.ms.id.monitor.model.DiscoveryResponseModel.LeaseInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Service
public class JmxService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JmxService.class);

	private final String STATU_UP = "UP";
	private final String STATU_DOWN = "DOWN";

	@Data
	public static class JmxApplication {

		private String name;
		private boolean available = false;

		private Map<String, JmxConnection> connections = new Hashtable<>();

		public JmxApplication(String name) {
			this.name = name;
		}
	}

	@Data
	public static class JmxConnection {

		private JmxApplication application;
		private String instanceId;
		private int port;
		private boolean jmxEnable = false;
		private JMXConnector connector;

		public JmxConnection(JmxApplication application) {
			this.application = application;
		}

		public <T> T read(String mBeanName, String operation, TypeReference<T> t) throws Exception {
			return read(mBeanName, operation, null, null, t);
		}

		public <T> T read(String mBeanName, String operation, Object[] params, String[] signatures, TypeReference<T> t)
				throws Exception {
			try {

				MBeanServerConnection mbsc = connector.getMBeanServerConnection();

				ObjectName mbeanName = new ObjectName(mBeanName);

				Map data = (Map) mbsc.invoke(mbeanName, operation, params, signatures);

				ObjectMapper mapper = new ObjectMapper();

				return mapper.convertValue(data, t);

			} catch (IOException | MBeanException | ReflectionException | InstanceNotFoundException
					| MalformedObjectNameException e) {

				throw new Exception(e);
			}

		}

	}

	@Autowired
	private MonitorDataService monitorDataService;

	@Autowired
	private OAuth2RestTemplate restTemplate;

	private Map<String, JmxApplication> jmxRegistory = new Hashtable<>();

	@Scheduled(fixedDelay = 6000)
	public void refresh() {

		LOGGER.debug("JMX Registry refreshing...");

		for (ApplicationEntity application : monitorDataService.getApplications()) {

			JmxApplication jmxApplication;

			if (jmxRegistory.containsKey(application.getName())) {

				jmxApplication = jmxRegistory.get(application.getName());

			} else {

				jmxApplication = new JmxApplication(application.getName());

				jmxRegistory.put(application.getName(), jmxApplication);
			}

			DiscoveryResponseModel.Application discoveryApplication = getDiscoveryApplication()
					.get(application.getName());

			if (discoveryApplication == null) {

				jmxApplication.setAvailable(false);

			} else {

				jmxApplication.setAvailable(true);

				Map<String, InstanceEntity> instanceEntities = monitorDataService.getInstances(application.getName());

				for (DiscoveryResponseModel.Instance instance : discoveryApplication.getInstance()) {

					InstanceEntity instanceEntity;

					if (!instanceEntities.containsKey(instance.getInstanceId())) {
						instanceEntity = monitorDataService.getInstance(instance.getInstanceId());
					} else {
						instanceEntity = instanceEntities.get(instance.getInstanceId());
					}

					Map<String, JmxConnection> connections = jmxApplication.getConnections();
					JmxConnection connection;

					if (connections.containsKey(instance.getInstanceId())) {

						connection = connections.get(instance.getInstanceId());

					} else {

						connection = new JmxConnection(jmxApplication);

						connections.put(instance.getInstanceId(), connection);
					}

					if (instance.getMetadata().containsKey("jmx.port")) {

						connection.setJmxEnable(true);
						connection.setInstanceId(instance.getInstanceId());
						connection.setPort(Integer.parseInt(instance.getMetadata().get("jmx.port")));

						try {

							connection.setConnector(createJmxConnection(connection.getPort()));

							LOGGER.debug("Registered connector {} ", instance.getInstanceId());

						} catch (IOException e) {

							LOGGER.error("Jmx registration error {} :- {}", instance.getInstanceId(), e.getMessage());

						}
					} else {

						connection.setJmxEnable(false);

						LOGGER.debug("{} JMX not available", instance.getInstanceId());
					}

					instanceEntity.setInstanceId(instance.getInstanceId());
					instanceEntity.setApp(instance.getApp());
					instanceEntity.setAppGroupName(instance.getAppGroupName());
					instanceEntity.setIpAddr(instance.getIpAddr());
					instanceEntity.setSid(instance.getSid());
					instanceEntity.setHomePageUrl(instance.getHomePageUrl());
					instanceEntity.setStatusPageUrl(instance.getStatusPageUrl());
					instanceEntity.setHealthCheckUrl(instance.getHealthCheckUrl());
					instanceEntity.setSecureHealthCheckUrl(instance.getSecureHealthCheckUrl());
					instanceEntity.setVipAddress(instance.getVipAddress());
					instanceEntity.setSecureVipAddress(instance.getSecureVipAddress());
					instanceEntity.setCountryId(instance.getCountryId());
					instanceEntity.setHostName(instance.getHostName());
					instanceEntity.setStatus(instance.getStatus());
					instanceEntity.setOverriddenStatus(instance.getOverriddenStatus());
					instanceEntity.setLeaseInfo(instance.getLeaseInfo());
					instanceEntity.setCoordinatingDiscoveryServer(instance.isCoordinatingDiscoveryServer());
					instanceEntity.setLastUpdatedTimestamp(instance.getLastUpdatedTimestamp());
					instanceEntity.setLastDirtyTimestamp(instance.getLastDirtyTimestamp());
					instanceEntity.setActionType(instance.getActionType());
					instanceEntity.setAsgName(instance.getAsgName());

					Map<String, String> metaData = new LinkedHashMap<>();
					if (instance.getMetadata() != null) {
						for (Map.Entry<String, String> entry : instance.getMetadata().entrySet()) {
							metaData.put(entry.getKey().replace(".", ""), entry.getValue());
						}
					}
					instanceEntity.setMetadata(metaData);

					monitorDataService.update(instanceEntity);

					instanceEntities.remove(instance.getInstanceId());
				}

				for (InstanceEntity instanceEntity : instanceEntities.values()) {
					instanceEntity.setStatus(STATU_DOWN);
					monitorDataService.update(instanceEntity);
				}

			}

			if (!jmxApplication.isAvailable()) {

				application.setStatus(STATU_DOWN);

			} else {

				application.setStatus(STATU_UP);

			}

			monitorDataService.update(application);
		}

		LOGGER.debug("JMX Registry refreshing is completed!");
	}

	public Collection<JmxApplication> getApplications() {
		return jmxRegistory.values();
	}

	private JMXConnector createJmxConnection(int port) throws IOException {

		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");

		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

		return jmxc;
	}

	private Map<String, DiscoveryResponseModel.Application> getDiscoveryApplication() {

		ResponseEntity<DiscoveryResponseModel> entity = restTemplate
				.getForEntity("http://my-cloud-discovery/discovery/applications", DiscoveryResponseModel.class);

		Map<String, DiscoveryResponseModel.Application> model = new HashMap<String, DiscoveryResponseModel.Application>();

		if (entity.getStatusCode() == HttpStatus.OK) {

			for (DiscoveryResponseModel.Application appModel : entity.getBody().getApplication()) {
				model.put(appModel.getName().toLowerCase(), appModel);
			}

		} else {
			LOGGER.error("Discovery application loading error {} ", entity.getStatusCode());
		}

		return model;
	}
}
