package com.developerhelperhub.ms.monitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.monitor.application.ApplicationEntity;
import com.developerhelperhub.ms.monitor.application.InstanceEntity;
import com.developerhelperhub.ms.monitor.model.DiscoveryResponseModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import lombok.Data;
import lombok.Getter;

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
		private String hostName;
		private String ipAddress;
		private int managementPort;
		private boolean managementEnable = false;
		private int jmxPort;
		private boolean jmxEnabled = false;
		private OAuth2RestTemplate template;

		public JmxConnection(JmxApplication application) {
			this.application = application;
		}

		public <T> T read(String mBeanName, String operation, TypeReference<T> responseType) throws Exception {
			return read(mBeanName, operation, null, responseType);
		}

		public <T> T read(String mBeanName, String operation, String[] params, TypeReference<T> responseType)
				throws Exception {

			String url = String.format("http://%s:%s/actuator/jolokia/exec/%s/%s", this.ipAddress, this.managementPort,
					mBeanName, operation);

			if (params != null) {
				url = url + "/" + String.join("/", params);
			}

			LOGGER.debug(" JMX exec {} - {} ", this.instanceId, url);

			ResponseEntity<String> entity = this.template.getForEntity(url, String.class);

			if (entity.getStatusCode() == HttpStatus.OK) {

				final JsonParser springParser = JsonParserFactory.getJsonParser();
				final Map<String, Object> map = springParser.parseMap(entity.getBody());

				if ((Integer) map.get("status") == 200) {

					ObjectMapper mapper = new ObjectMapper();

					return mapper.convertValue((Map<String, Object>) map.get("value"), responseType);

				} else {

					throw new RuntimeException(
							String.format("Enpoint Jmx Response error %s - %s", url, entity.getBody()));

				}

			} else {
				throw new RuntimeException(
						String.format("Enpoint Response error %s - %d ", url, entity.getStatusCode()));
			}

		}

	}

	@Autowired
	private MonitorDataService monitorDataService;

	@Autowired
	@Qualifier("oAuthLoadBalance")
	private OAuth2RestTemplate restTemplate;

	@Autowired
	@Qualifier("oAuthNonLoadBalance")
	private OAuth2RestTemplate restTemplateNonLoadBalance;

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

			Map<String, InstanceEntity> instanceEntities = monitorDataService.getInstances(application.getName());

			int totalInstance = instanceEntities.size();
			int runningInstance = 0;

			if (discoveryApplication == null) {

				jmxApplication.setAvailable(false);

			} else {

				jmxApplication.setAvailable(true);

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

					connection.setIpAddress(instance.getIpAddr());
					connection.setInstanceId(instance.getInstanceId());
					connection.setHostName(instance.getHostName());
					connection.setTemplate(restTemplateNonLoadBalance);

					if (instance.getMetadata().containsKey("management.port")) {

						connection.setManagementEnable(true);
						connection.setManagementPort(Integer.parseInt(instance.getMetadata().get("management.port")));

					} else {

						connection.setManagementEnable(false);

						LOGGER.debug("{} Mangement endpoint not available", instance.getInstanceId());
					}

					if (instance.getMetadata().containsKey("jmx.port")) {

						connection.setJmxEnabled(true);
						connection.setJmxPort(Integer.parseInt(instance.getMetadata().get("jmx.port")));

					} else {

						connection.setJmxEnabled(false);

						LOGGER.debug("{} Jmx endpoint not available", instance.getInstanceId());
					}

					instanceEntity.setInstanceId(instance.getInstanceId());
					instanceEntity.setApplication(instance.getApp().toLowerCase());
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

					runningInstance++;
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

			application.setTotalInstance(totalInstance);
			application.setRunningInstance(runningInstance);

			monitorDataService.update(application);
		}

		LOGGER.debug("JMX Registry refreshing is completed!");
	}

	public Collection<JmxApplication> getApplications() {
		return jmxRegistory.values();
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
