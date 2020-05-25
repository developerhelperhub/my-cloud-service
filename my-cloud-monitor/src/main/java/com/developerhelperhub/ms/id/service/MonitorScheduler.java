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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

import com.developerhelperhub.ms.id.service.application.ApplicationDiskSpace;
import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;
import com.developerhelperhub.ms.id.service.discovery.DiscoveryResponseModel;
import com.developerhelperhub.ms.id.service.health.HealthResponseModel;
import com.developerhelperhub.ms.id.service.info.ApplicationInfo;
import com.developerhelperhub.ms.id.service.metrics.MatricModel;

@Component
public class MonitorScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorScheduler.class);

	@Autowired
	private MonitorApplication applicationSerivice;

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@Autowired
	private InfluxDB influxDB;

	@Scheduled(fixedDelay = 1000)
	public void scheduleMonitorInfo() {
		monitorInfo();
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduleUpdateDiscoverInformation() {
		updateDiscoverInformation();
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduleMmonitorHealth() {
		monitorHealth();
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduleMonitorMatrics() {
		monitorMatrics();
	}

	public void monitorInfo() {

		applicationSerivice.get().parallelStream().forEach(app -> {
			writeInfo(app);
		});
	}

	public void updateDiscoverInformation() {

		Map<String, DiscoveryResponseModel.Application> discovery = getDiscoveryApplication();

		applicationSerivice.get().parallelStream().forEach(app -> {

			DiscoveryResponseModel.Application application = discovery.get(app.getName());

			if (application != null) {

				Map<String, DiscoveryResponseModel.Instance> instances = new HashMap<>();

				for (DiscoveryResponseModel.Instance instance : application.getInstance()) {
					instances.put(instance.getInstanceId(), instance);
				}

				for (DiscoveryResponseModel.Instance instance : app.getInstance()) {

					DiscoveryResponseModel.Instance appInstance = instances.get(instance.getInstanceId());

					instance.setStatus("DOWN");

					if (appInstance != null) {
						instances.put(instance.getInstanceId(), instance);
					}

				}

				app.setInstance(instances.values());

				applicationSerivice.update(app);

			}

		});
	}

	public void monitorHealth() {

		applicationSerivice.get().parallelStream().forEach(app -> {
			writeHealth(app);
		});
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
			LOGGER.error("Discovery application response error {} ", entity.getStatusCode());
		}

		return model;
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

	private Set<String> getMemoryMatrics() {
		Set<String> matrics = new TreeSet<>();

		matrics.add(MATRIX_JVM_MEMORY_USED);
		matrics.add(MATRIX_JVM_MEMORY_MAX);
		// matrics.add(MATRIX_JVM_MEMORY_COMMITED);

		matrics.add(MATRIX_JVM_BUFFER_MEMORY_PROMPTED);
		// matrics.add(MATRIX_JVM_BUFFER_COUNT);
		matrics.add(MATRIX_JVM_BUFFER_TOTAL_CAPACITY);

		// matrics.add(MATRIX_JVM_GC_MEMORY_ALLOCATED);
		// matrics.add(MATRIX_JVM_GC_MEMORY_PROMPTED);

		return matrics;
	}

	private Set<String> getThreadsMatrics() {
		Set<String> matrics = new TreeSet<>();

		matrics.add(MATRIX_JVM_THREADS_DAEMON);
		matrics.add(MATRIX_JVM_THREADS_LIVE);
		matrics.add(MATRIX_JVM_THREADS_PEAK);

		return matrics;
	}

	private Set<String> getMatrics() {
		Set<String> matrics = new TreeSet<>();

		matrics.addAll(getMemoryMatrics());
		matrics.addAll(getThreadsMatrics());

		return matrics;
	}

	public static final String MATRIX_JVM_MEMORY_USED = "jvm.memory.used";
	public static final String MATRIX_JVM_MEMORY_MAX = "jvm.memory.max";
	public static final String MATRIX_JVM_MEMORY_COMMITED = "jvm.memory.committed";

	public static final String MATRIX_JVM_BUFFER_MEMORY_PROMPTED = "jvm.buffer.memory.used";
	public static final String MATRIX_JVM_BUFFER_COUNT = "jvm.buffer.count";
	public static final String MATRIX_JVM_BUFFER_TOTAL_CAPACITY = "jvm.buffer.total.capacity";

	public static final String MATRIX_JVM_GC_MEMORY_ALLOCATED = "jvm.gc.memory.allocated";
	public static final String MATRIX_JVM_GC_MEMORY_PROMPTED = "jvm.gc.memory.promoted";

	public static final String MATRIX_JVM_THREADS_DAEMON = "jvm.threads.daemon";
	public static final String MATRIX_JVM_THREADS_LIVE = "jvm.threads.live";
	public static final String MATRIX_JVM_THREADS_PEAK = "jvm.threads.peak";

	public void monitorMatrics() {

		Set<String> matrics = getMatrics();

		applicationSerivice.get().parallelStream().forEach(app -> {

			matrics.parallelStream().forEach(matric -> {

				if (getMemoryMatrics().contains(matric)) {
					writeMatric(app, matric, "memory");
				} else if (getThreadsMatrics().contains(matric)) {
					writeMatric(app, matric, "thread");
				}

			});

		});

	}

	public void writeMatric(ApplicationEntity application, String metric, String measurementName) {

		ResponseEntity<MatricModel> response = restTemplate
				.getForEntity("http://" + application.getName() + "/actuator/metrics/" + metric, MatricModel.class);

		if (response.getStatusCode() == HttpStatus.OK) {

			MatricModel body = response.getBody();

			Point.Builder builder = Point.measurement(measurementName)
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("metric", metric)
					.addField("application", application.getName()).addField("baseUnit", body.getBaseUnit());

			for (MatricModel.Measurement measurement : body.getMeasurements()) {

				builder.addField("statistic", measurement.getStatistic()).addField("value", measurement.getValue());

			}

			influxDB.write(builder.build());

			LOGGER.debug("Matrics inserted value into {} {} {} !", measurementName, application, metric);

			influxDB.close();

		} else {

			LOGGER.error("Matrics response error {} {} {}", application, metric, response.getStatusCode());

		}
	}

	public void writeInfo(ApplicationEntity application) {

		final String measurementName = "info";

		ResponseEntity<ApplicationInfo> response = restTemplate
				.getForEntity("http://" + application.getName() + "/actuator/info", ApplicationInfo.class);

		if (response.getStatusCode() == HttpStatus.OK) {

			ApplicationInfo body = response.getBody();

			LOGGER.debug("Info of {} ", application);

			Point.Builder builder = Point.measurement(measurementName)
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("application", application.getName())
					.addField("build_version", body.getBuild().getVersion())
					.addField("build_artifact", body.getBuild().getArtifact())
					.addField("build_artifact", body.getBuild().getArtifact())
					.addField("build_name", body.getBuild().getArtifact())
					.addField("build_time", body.getBuild().getArtifact())
					.addField("build_group", body.getBuild().getArtifact());

			influxDB.write(builder.build());

			LOGGER.debug("Info inserted value into {} {} !", measurementName, application);

			influxDB.close();

			application.setBuild(body.getBuild());

			applicationSerivice.update(application);
		} else {

			LOGGER.error("Info response error {} {}", application, response.getStatusCode());

		}

	}
}
