package com.developerhelperhub.ms.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBConfiguration.class);

	public static final String RETENTION_DEFAULT_POLICY = "defaultPolicy";

	public static final String RETENTION_REAL_TIME_POLICY = "defaultRealTimePolicy";

	@Value("${mycloud.influxdb.host}")
	private String host;

	@Value("${mycloud.influxdb.port}")
	private String port;

	@Value("${mycloud.influxdb.username}")
	private String username;

	@Value("${mycloud.influxdb.password}")
	private String password;

	@Value("${mycloud.influxdb.database}")
	private String database;
	
	@Bean
	public InfluxDB influxDB() {

		final String url = "http://" + this.host + ":" + this.port;

		
		LOGGER.info("InfluxDB host: {}", this.host);
		LOGGER.info("InfluxDB port: {}", this.port);
		LOGGER.info("InfluxDB url: {}", url);
		LOGGER.debug("InfluxDB username: {}", this.username);
		LOGGER.debug("InfluxDB password: {}", this.password);
		
		InfluxDB influxDB = InfluxDBFactory.connect(url, this.username, this.password);

		Pong response = influxDB.ping();

		if (response.getVersion().equalsIgnoreCase("unknown")) {
			LOGGER.error("InfluxDB is not able to connect!");
			return null;
		}

		influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
		influxDB.createDatabase(this.database);
		influxDB.createRetentionPolicy(RETENTION_DEFAULT_POLICY, this.database, "2d", 1, true);
		influxDB.createRetentionPolicy(RETENTION_REAL_TIME_POLICY, this.database, "1h", 1, true);

		influxDB.setRetentionPolicy(InfluxDBConfiguration.RETENTION_REAL_TIME_POLICY);
		influxDB.setDatabase(this.database);

		LOGGER.info("InfluxDB is connected!");

		return influxDB;
	}

}
