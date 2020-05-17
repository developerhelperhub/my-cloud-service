package com.developerhelperhub.ms.id.service;

import java.util.ArrayList;
import java.util.List;
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

import com.developerhelperhub.ms.id.service.metrics.JvmMemoryUsedModel;

@Component
public class MonitorScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorScheduler.class);

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@Autowired
	private InfluxDB influxDB;

	private List<String> getApplications() {
		List<String> applications = new ArrayList<>();

		ResponseEntity<DiscoveryResponseModel> entity = restTemplate
				.getForEntity("http://my-cloud-discovery/discovery/applications", DiscoveryResponseModel.class);

		if (entity.getStatusCode() == HttpStatus.OK) {

			entity.getBody().getApplication().forEach(app -> {
				applications.add(app.getName());
			});

		} else {
			LOGGER.error("Discovery application response error {} ", entity.getStatusCode());
		}
		return applications;
	}

	private final String MATRIX_JVM_MEMORY_USED = "jvm.memory.used";

	private Set<String> getMatrics() {
		Set<String> matrics = new TreeSet<>();

		matrics.add(MATRIX_JVM_MEMORY_USED);

		return matrics;
	}

	//@Scheduled(fixedDelay = 1000)
	public void monitor() {

		List<String> applications = getApplications();
		Set<String> matrics = getMatrics();

		applications.parallelStream().forEach(app -> {

			matrics.parallelStream().forEach(matric -> {

				if (matric.equals(MATRIX_JVM_MEMORY_USED)) {
					jvmMmemoryUsed(app, matric);
				}

			});

		});

		System.out.println(applications);
	}

	public void jvmMmemoryUsed(String application, String metric) {

		String measurementName = "memory";

		ResponseEntity<JvmMemoryUsedModel> response = restTemplate
				.getForEntity("http://" + application + "/actuator/metrics/" + metric, JvmMemoryUsedModel.class);

		if (response.getStatusCode() == HttpStatus.OK) {

			JvmMemoryUsedModel body = response.getBody();

			for (JvmMemoryUsedModel.Measurement measurement : body.getMeasurements()) {

				Point point = Point.measurement(measurementName).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.addField("metric", metric).addField("application", application)
						.addField("statistic", measurement.getStatistic()).addField("value", measurement.getValue())
						.build();

				influxDB.write(point);

				LOGGER.debug("Inserted value into {} {} {} !", measurementName, application, metric);
			}

			influxDB.close();

		} else {

			LOGGER.error("Matrix response error {} {} {}", application, metric, response.getStatusCode());

		}
	}

}
