package com.developerhelperhub.ms.id.monitor.actuator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.influxdb.InfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.monitor.actuator.info.HealthMonitor;
import com.developerhelperhub.ms.id.monitor.actuator.info.InfoMonitor;
import com.developerhelperhub.ms.id.monitor.actuator.info.MonitorDataService;
import com.developerhelperhub.ms.id.service.JmxService;
import com.developerhelperhub.ms.id.service.JmxService.JmxConnection;

@Service
public class ActuatorJmxEndpointScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorJmxEndpointScheduler.class);

	@Autowired
	private JmxService jmxService;

	@Autowired
	private MonitorDataService dataService;

	@Autowired
	private InfluxDB influxDB;

	private List<ActuatorJmxMonitor> monitors = new ArrayList<>();

	public ActuatorJmxEndpointScheduler() {

		monitors.add(new InfoMonitor("org.springframework.boot:type=Endpoint,name=Info", "info"));
		monitors.add(new HealthMonitor("org.springframework.boot:type=Endpoint,name=Health", "health"));

	}

	@Scheduled(fixedDelay = 1000)
	public void process() {

		Collection<JmxConnection> connections = jmxService.getServices();

		connections.parallelStream().forEach(connection -> {

			monitors.parallelStream().forEach(monitor -> {

				LOGGER.debug("{}:- {}:({}) ", connection.getInstanceId(), monitor.getMBeanName(),
						monitor.getOperation());

				monitor.setDataService(dataService);
				monitor.setInfluxDB(influxDB);
				monitor.setConnection(connection);

				monitor.process();
			});
		});

	}

}
