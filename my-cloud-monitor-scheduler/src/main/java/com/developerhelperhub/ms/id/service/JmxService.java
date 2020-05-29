package com.developerhelperhub.ms.id.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Service
public class JmxService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JmxService.class);

	@Data
	public static class JmxConnection {

		private String serivce;
		private String instanceId;
		private int port;
		private JMXConnector connector;

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
	private DiscoveryClient discoveryClient;

	private Map<String, JmxConnection> jmxRegistory = new Hashtable<>();

	public Collection<JmxConnection> getServices() {

		Set<String> closedServices = new HashSet<>(jmxRegistory.keySet());

		for (String serivce : discoveryClient.getServices()) {

			for (ServiceInstance instance : discoveryClient.getInstances(serivce)) {

				if (instance.getMetadata().containsKey("jmx.port")) {

					JmxConnection connection;

					if (!jmxRegistory.containsKey(instance.getInstanceId())) {

						connection = new JmxConnection();
						connection.setSerivce(serivce);
						connection.setInstanceId(instance.getInstanceId());
						connection.setPort(Integer.parseInt(instance.getMetadata().get("jmx.port")));

						try {

							connection.setConnector(createJmxConnection(connection.getPort()));

							jmxRegistory.put(instance.getInstanceId(), connection);

							LOGGER.debug("Registered connector {} ", instance.getInstanceId());

						} catch (IOException e) {

							LOGGER.error("Jmx registration error {} :- {}", instance.getInstanceId(), e.getMessage());

						}

					} else {

						connection = jmxRegistory.get(instance.getInstanceId());

						LOGGER.debug("Loaded connector {} ", instance.getInstanceId());
					}

					closedServices.remove(instance.getInstanceId());

				} else {

					LOGGER.debug("{} JMX not available", instance.getInstanceId());

				}
			}

		}

		for (String instanceId : closedServices) {

			jmxRegistory.remove(instanceId);

			LOGGER.debug("{} Removed JMX connection from Registory", instanceId);

		}

		return jmxRegistory.values();
	}

	private JMXConnector createJmxConnection(int port) throws IOException {

		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");

		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

		return jmxc;
	}
}
