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
import com.developerhelperhub.ms.id.service.JmxService.JmxApplication;
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

		LOGGER.debug("Monitor process is starting ..... ");

		Collection<JmxApplication> connections = jmxService.getApplications();

		connections.parallelStream().forEach(app -> {
			LOGGER.debug("Application process is starting {} : ({}) ....", app.getName(), app.isAvailable());

			app.getConnections().values().parallelStream().forEach(con -> {

				LOGGER.debug("Instance process is starting {} : ({}) ...", con.getInstanceId(), con.isJmxEnable());

				LOGGER.debug("IInstance process is completed {} : ({})", con.getInstanceId(), con.isJmxEnable());

			});

			LOGGER.debug("Application process is completed {} : ({})!", app.getName(), app.isAvailable());
		});

		LOGGER.debug("Monitor process is completed !");

//		connections.parallelStream().forEach(app -> {
//
//			if (app.isAvailable()) {
//
//				app.getConnections().values().parallelStream().forEach(con -> {
//
//					if (con.isJmxEnable()) {
//
//						monitors.parallelStream().forEach(monitor -> {
//
//							LOGGER.debug("{}:- {}:({}) ", con.getInstanceId(), monitor.getMBeanName(),
//									monitor.getOperation());
//
//							monitor.setDataService(dataService);
//							monitor.setInfluxDB(influxDB);
//							monitor.setConnection(con);
//
//							try {
//
//								monitor.process();
//
//							} catch (Exception exception) {
//								LOGGER.debug("{}:- {}:({}) - ERROR: {}", con.getInstanceId(), monitor.getMBeanName(),
//										monitor.getOperation(), exception.getMessage());
//							}
//
//						});
//
//					}
//
//				});
//
//			}
//		});

	}

}
