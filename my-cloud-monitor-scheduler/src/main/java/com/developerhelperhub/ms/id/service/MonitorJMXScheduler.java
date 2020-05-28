package com.developerhelperhub.ms.id.service;

import java.io.IOException;
import java.net.MalformedURLException;
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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;
import com.developerhelperhub.ms.id.service.info.ApplicationInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MonitorJMXScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorScheduler.class);

	@Autowired
	private MonitorApplication applicationSerivice;

	@Autowired
	private DiscoveryClient discoveryClient;

	// @Scheduled(fixedDelay = 1000)
	public void monitorInfo() {

		for (String serivce : discoveryClient.getServices()) {

			LOGGER.debug("--------{}--------", serivce);

			for (ServiceInstance instance : discoveryClient.getInstances(serivce)) {

				if (instance.getMetadata().containsKey("jmx.port")) {

					writeInfo(Integer.parseInt(instance.getMetadata().get("jmx.port")));

				}
			}
		}
	}

	private void writeInfo(int port) {

		try {

			JMXConnector jmxc = createJmxConnection(port);

			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			ObjectName mbeanName = new ObjectName("org.springframework.boot:type=Endpoint,name=Info");

			Map data = (Map) mbsc.invoke(mbeanName, "info", null, null);

			ObjectMapper mapper = new ObjectMapper();

			ApplicationInfo info = mapper.convertValue(data, ApplicationInfo.class);

			System.err.println(info.getBuild().getArtifact());

			jmxc.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JMXConnector createJmxConnection(int port) throws IOException {

		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");

		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

		return jmxc;
	}
}
