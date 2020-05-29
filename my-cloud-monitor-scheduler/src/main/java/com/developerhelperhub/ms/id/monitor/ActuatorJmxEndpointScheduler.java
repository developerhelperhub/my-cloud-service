package com.developerhelperhub.ms.id.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.service.JmxService;
import com.developerhelperhub.ms.id.service.JmxService.JmxConnection;
import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;

@Service
public class ActuatorJmxEndpointScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorScheduler.class);

	@Autowired
	private JmxService jmxService;

	@Autowired
	private MonitorApplication applicationSerivice;

	private List<ActuatorJmxMonitor> monitors = new ArrayList<>();

	public ActuatorJmxEndpointScheduler() {

		monitors.add(new InfoMonitor("org.springframework.boot:type=Endpoint,name=Info", "info"));

	}

	@Scheduled(fixedDelay = 1000)
	public void process() {

		Collection<JmxConnection> connections = jmxService.getServices();

		connections.parallelStream().forEach(instance -> {

			ApplicationEntity application = applicationSerivice.get(instance.getSerivce());

			monitors.stream().forEach(monitor -> {

				LOGGER.debug("{}:- {}:({}) ", instance.getInstanceId(), monitor.getMBeanName(), monitor.getOperation());

				monitor.process(instance);
			});
		});

	}

}
