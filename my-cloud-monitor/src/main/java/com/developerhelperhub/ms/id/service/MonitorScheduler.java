package com.developerhelperhub.ms.id.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.ApplicationEntity.ApplicationDiskSpace;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;
import com.developerhelperhub.ms.id.service.discovery.DiscoveryResponseModel;
import com.developerhelperhub.ms.id.service.health.HealthResponseModel;
import com.developerhelperhub.ms.id.service.metrics.MemoryModel;

@Component
public class MonitorScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorScheduler.class);

	@Autowired
	private MonitorApplication applicationSerivice;

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@Autowired
	private InfluxDB influxDB;

	private final String MATRIX_JVM_MEMORY_USED = "jvm.memory.used";
	private final String MATRIX_JVM_MEMORY_MAX = "jvm.memory.max";
	private final String MATRIX_JVM_MEMORY_COMMITED = "jvm.memory.committed";
	private final String MATRIX_JVM_GC_MEMORY_PROMPTED = "jvm.gc.memory.promoted";
	private final String MATRIX_JVM_BUFFER_MEMORY_PROMPTED = "jvm.buffer.memory.used";
	private final String MATRIX_JVM_GC_MEMORY_ALLOCATED = "jvm.gc.memory.allocated";

	private Map<String, DiscoveryResponseModel.Application> getDiscoveryApplication() {

		ResponseEntity<DiscoveryResponseModel> entity = restTemplate
				.getForEntity("http://my-cloud-discovery/discovery/applications", DiscoveryResponseModel.class);

		Map<String, DiscoveryResponseModel.Application> model = new HashMap<String, DiscoveryResponseModel.Application>();

		if (entity.getStatusCode() == HttpStatus.OK) {

			for (DiscoveryResponseModel.Application appModel : entity.getBody().getApplication()) {
				model.put(appModel.getName().toLowerCase(), appModel);
			}

		} else {
			LOGGER.error("Discovery application response error {} ", entity.getStatusCode());
		}

		return model;
	}

	// @Scheduled(fixedDelay = 1000)
	public void updateDiscoverInformation() {

		Map<String, DiscoveryResponseModel.Application> discovery = getDiscoveryApplication();

		applicationSerivice.get().parallelStream().forEach(app -> {

			DiscoveryResponseModel.Application application = discovery.get(app.getName());

			if (application != null) {

				app.setInstance(application.getInstance());

				applicationSerivice.update(app);

			}

		});
	}

	// @Scheduled(fixedDelay = 1000)
	public void monitorHealth() {

		applicationSerivice.get().parallelStream().forEach(app -> {
			writeHealth(app);
		});
	}

	public void writeHealth(ApplicationEntity application) {

		final String measurementName = "health";
		final String STATUS_DOWN = "DOWN";

		ResponseEntity<HealthResponseModel> response = restTemplate
				.getForEntity("http://" + application.getName() + "/actuator/health", HealthResponseModel.class);

		Point.Builder builder = Point.measurement(measurementName)
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("application", application.getName());

		if (response.getStatusCode() == HttpStatus.OK) {

			HealthResponseModel body = response.getBody();

			LOGGER.debug("Helath of {} : {}", application, body.toString());

			builder.addField("status", body.getStatus()).addField("disk_space_status",
					body.getComponents().getDiskSpace().getStatus());

			application.setStatus(body.getStatus());

			if (body.getComponents() != null) {

				if (body.getComponents().getDiskSpace() != null) {

					ApplicationDiskSpace diskSpace = new ApplicationDiskSpace();

					diskSpace.setFree(body.getComponents().getDiskSpace().getDetails().get("free"));
					diskSpace.setThreshold(body.getComponents().getDiskSpace().getDetails().get("threshold"));
					diskSpace.setTotal(body.getComponents().getDiskSpace().getDetails().get("total"));
					diskSpace.setStatus(body.getComponents().getDiskSpace().getStatus());

					builder.addField("disk_space_total", diskSpace.getTotal())
							.addField("disk_space_free", diskSpace.getFree())
							.addField("disk_space_threshold", diskSpace.getThreshold());

					application.setDiskSpace(diskSpace);
				}

				if (body.getComponents().getHystrix() != null) {
					builder.addField("hystrix_status", body.getComponents().getHystrix().getStatus());
				}
			}

		} else {

			builder.addField("status", STATUS_DOWN);

			application.setStatus(STATUS_DOWN);

			LOGGER.error("Health response error {} {}", application, response.getStatusCode());

		}

		influxDB.write(builder.build());

		LOGGER.debug("Helath inserted value into {} {} !", measurementName, application);

		influxDB.close();

		applicationSerivice.update(application);
	}

	// @Scheduled(fixedDelay = 1000)
	public void monitorMatrics() {

		Set<String> matrics = getMatrics();

		applicationSerivice.get().parallelStream().forEach(app -> {

			matrics.parallelStream().forEach(matric -> {

				if (matric.equals(MATRIX_JVM_MEMORY_USED) || matric.equals(MATRIX_JVM_MEMORY_MAX)
						|| matric.equals(MATRIX_JVM_MEMORY_COMMITED) || matric.equals(MATRIX_JVM_GC_MEMORY_PROMPTED)
						|| matric.equals(MATRIX_JVM_BUFFER_MEMORY_PROMPTED)
						|| matric.equals(MATRIX_JVM_GC_MEMORY_ALLOCATED)) {
					writeMmemory(app, matric);
				}

			});

		});

	}

	private Set<String> getMatrics() {
		Set<String> matrics = new TreeSet<>();

		matrics.add(MATRIX_JVM_MEMORY_USED);
		matrics.add(MATRIX_JVM_MEMORY_MAX);
		matrics.add(MATRIX_JVM_MEMORY_COMMITED);
		matrics.add(MATRIX_JVM_GC_MEMORY_PROMPTED);
		matrics.add(MATRIX_JVM_BUFFER_MEMORY_PROMPTED);
		matrics.add(MATRIX_JVM_GC_MEMORY_ALLOCATED);

		return matrics;
	}

	public void writeMmemory(ApplicationEntity application, String metric) {

		String measurementName = "memory";

		ResponseEntity<MemoryModel> response = restTemplate
				.getForEntity("http://" + application.getName() + "/actuator/metrics/" + metric, MemoryModel.class);

		if (response.getStatusCode() == HttpStatus.OK) {

			MemoryModel body = response.getBody();

			Point.Builder builder = Point.measurement(measurementName)
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("metric", metric)
					.addField("application", application.getName()).addField("baseUnit", body.getBaseUnit());

			for (MemoryModel.Measurement measurement : body.getMeasurements()) {

				builder.addField("statistic", measurement.getStatistic()).addField("value", measurement.getValue());

			}

			influxDB.write(builder.build());

			LOGGER.debug("Matrics inserted value into {} {} {} !", measurementName, application, metric);

			influxDB.close();

		} else {

			LOGGER.error("Matrics response error {} {} {}", application, metric, response.getStatusCode());

		}
	}

}