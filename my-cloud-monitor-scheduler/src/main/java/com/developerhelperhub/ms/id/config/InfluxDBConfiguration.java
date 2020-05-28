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

	public static final String DATABASE_NAME = "mycloudmonitordb";

	public static final String RETENTION_DEFAULT_POLICY = "defaultPolicy";

	public static final String RETENTION_REAL_TIME_POLICY = "defaultRealTimePolicy";

	@Bean
	public InfluxDB influxDB() {

		InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "todd", "influxdb4ever");

		Pong response = influxDB.ping();

		if (response.getVersion().equalsIgnoreCase("unknown")) {
			LOGGER.error("InfluxDB is not able to connect!");
			return null;
		}

		influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
		influxDB.createDatabase(DATABASE_NAME);
		influxDB.createRetentionPolicy(RETENTION_DEFAULT_POLICY, DATABASE_NAME, "2d", 1, true);
		influxDB.createRetentionPolicy(RETENTION_REAL_TIME_POLICY, DATABASE_NAME, "1h", 1, true);

		influxDB.setRetentionPolicy(InfluxDBConfiguration.RETENTION_REAL_TIME_POLICY);
		influxDB.setDatabase(DATABASE_NAME);

		LOGGER.info("InfluxDB is connected!");

		return influxDB;
	}

}
