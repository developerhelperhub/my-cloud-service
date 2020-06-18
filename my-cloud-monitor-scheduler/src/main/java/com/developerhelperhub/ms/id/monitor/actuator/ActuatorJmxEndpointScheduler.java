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

import com.developerhelperhub.ms.id.monitor.JmxService;
import com.developerhelperhub.ms.id.monitor.JmxService.JmxApplication;
import com.developerhelperhub.ms.id.monitor.MonitorDataService;
import com.developerhelperhub.ms.id.monitor.actuator.health.HealthMonitor;
import com.developerhelperhub.ms.id.monitor.actuator.info.InfoMonitor;

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

	private final String METRIX_JVM_MEMORY_USED = "jvm.memory.used";
	private final String METRIX_JVM_MEMORY_MAX = "jvm.memory.max";
	private final String METRIX_JVM_MEMORY_COMMITED = "jvm.memory.committed";

	private final String METRIX_JVM_BUFFER_MEMORY_PROMPTED = "jvm.buffer.memory.used";
	private final String METRIX_JVM_BUFFER_COUNT = "jvm.buffer.count";
	private final String METRIX_JVM_BUFFER_TOTAL_CAPACITY = "jvm.buffer.total.capacity";

	private final String METRIX_JVM_GC_MEMORY_ALLOCATED = "jvm.gc.memory.allocated";
	private final String METRIX_JVM_GC_MEMORY_PROMPTED = "jvm.gc.memory.promoted";

	private final String METRIX_JVM_THREADS_DAEMON = "jvm.threads.daemon";
	private final String METRIX_JVM_THREADS_LIVE = "jvm.threads.live";
	private final String METRIX_JVM_THREADS_PEAK = "jvm.threads.peak";

	public ActuatorJmxEndpointScheduler() {

		monitors.add(new InfoMonitor("org.springframework.boot:type=Endpoint,name=Info", "info", "info"));
		monitors.add(new HealthMonitor("org.springframework.boot:type=Endpoint,name=Health", "health", "health"));

		monitors.add(new MetricMonitor("org.springframework.boot:type=Endpoint,name=Metrics", "metric",
				new String[] { METRIX_JVM_MEMORY_USED, "null" }, "memory"));

		monitors.add(new MetricMonitor("org.springframework.boot:type=Endpoint,name=Metrics", "metric",
				new String[] { METRIX_JVM_MEMORY_MAX, "null" }, "memory"));

		monitors.add(new MetricMonitor("org.springframework.boot:type=Endpoint,name=Metrics", "metric",
				new String[] { METRIX_JVM_BUFFER_MEMORY_PROMPTED, "null" }, "memory"));

		monitors.add(new MetricMonitor("org.springframework.boot:type=Endpoint,name=Metrics", "metric",
				new String[] { METRIX_JVM_BUFFER_TOTAL_CAPACITY, "null" }, "memory"));

		monitors.add(new MetricMonitor("org.springframework.boot:type=Endpoint,name=Metrics", "metric",
				new String[] { METRIX_JVM_THREADS_DAEMON, "null" }, "thread"));

		monitors.add(new MetricMonitor("org.springframework.boot:type=Endpoint,name=Metrics", "metric",
				new String[] { METRIX_JVM_THREADS_LIVE, "null" }, "thread"));

		monitors.add(new MetricMonitor("org.springframework.boot:type=Endpoint,name=Metrics", "metric",
				new String[] { METRIX_JVM_THREADS_PEAK, "null" }, "thread"));

	}

	@Scheduled(fixedDelay = 1000)
	public void process() {

		LOGGER.debug("Monitor process is starting ..... ");

		Collection<JmxApplication> connections = jmxService.getApplications();

		connections.parallelStream().forEach(app -> {
			LOGGER.debug("Application process is starting {} : ({}) ....", app.getName(), app.isAvailable());

			app.getConnections().values().parallelStream().forEach(con -> {

				LOGGER.debug("Instance process is starting {} - Jmx Port {} : ({}) ...", con.getInstanceId(),
						con.getJmxPort(), con.isJmxEnabled());

				if (con.isJmxEnabled()) {

					monitors.parallelStream().forEach(monitor -> {

						LOGGER.debug("{}:- {}:({}) ", con.getInstanceId(), monitor.getMBeanName(),
								monitor.getOperation());

						monitor.setDataService(dataService);
						monitor.setInfluxDB(influxDB);
						monitor.setConnection(con);

						try {

							monitor.process();

						} catch (Exception exception) {
							LOGGER.debug("{}:- {}:({}) - ERROR: {}", con.getInstanceId(), monitor.getMBeanName(),
									monitor.getOperation(), exception.getMessage());
						}

					});

				}

				LOGGER.debug("IInstance process is completed {} : ({})", con.getInstanceId(), con.isJmxEnabled());

			});

			LOGGER.debug("Application process is completed {} : ({})!", app.getName(), app.isAvailable());
		});

		LOGGER.debug("Monitor process is completed !");

	}

}
