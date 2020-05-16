package com.developerhelperhub.ms.id.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBConfiguration.class);

	@Bean
	public InfluxDB influxDB() {

		InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "todd", "influxdb4ever");

		Pong response = influxDB.ping();

		if (response.getVersion().equalsIgnoreCase("unknown")) {
			LOGGER.error("InfluxDB is not able to connect!");
			return null;
		}

		influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
		influxDB.createDatabase("my-cloud-monitor-db");
		influxDB.createRetentionPolicy("defaultPolicy", "my-cloud-monitor-db", "2d", 1, true);

		LOGGER.info("InfluxDB is connected!");

		return influxDB;
	}

}
