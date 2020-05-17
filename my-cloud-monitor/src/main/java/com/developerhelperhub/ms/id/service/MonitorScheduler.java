package com.developerhelperhub.ms.id.service;

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

	// @Scheduled(fixedDelay = 1000)
	public void monitor() {

		ResponseEntity<DiscoveryResponseModel> entity = restTemplate
				.getForEntity("http://my-cloud-discovery/discovery/applications", DiscoveryResponseModel.class);

		if (entity.getStatusCode() == HttpStatus.OK) {

			entity.getBody().getApplication().parallelStream().forEach(app -> {

				System.err.println("--------> " + app.getName());

				ResponseEntity<String> health = restTemplate
						.getForEntity("http://" + app.getName() + "/actuator/health", String.class);

				if (health.getStatusCode() == HttpStatus.OK) {

					System.err.println("= " + health.getBody());

				} else {
					System.err.print(health.getStatusCode());
				}

			});
		} else {
			System.err.print(entity.getStatusCode());
		}
	}

	//@Scheduled(fixedDelay = 1000)
	public void jvmMmemoryUsed() {
		String serviceName = "my-cloud-discovery";
		String matricName = "jvm.memory.used";
		String measurementName = "memory";

		ResponseEntity<JvmMemoryUsedModel> response = restTemplate
				.getForEntity("http://" + serviceName + "/actuator/metrics/" + matricName, JvmMemoryUsedModel.class);

		if (response.getStatusCode() == HttpStatus.OK) {

			JvmMemoryUsedModel body = response.getBody();

			for (JvmMemoryUsedModel.Measurement measurement : body.getMeasurements()) {

				LOGGER.debug("Inserting value into {}...", measurementName);

				LOGGER.debug("statistic: {}, value: {}", measurement.getStatistic(), measurement.getValue());

				Point point = Point.measurement(measurementName).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.addField("statistic", measurement.getStatistic()).addField("value", measurement.getValue())
						.build();

				influxDB.write(point);

				LOGGER.debug("Inserted value into {}!", measurementName);
			}

			influxDB.close();
		}
	}
}
