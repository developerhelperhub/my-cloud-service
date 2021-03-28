package com.developerhelperhub.ms.entity.influxdb;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.Data;

@Data
@Measurement(name = "thread")
public class ThreadEntity {

	@Column(name = "time")
	private Instant time;

	@Column(name = "host_name", tag = true)
	private String hostName;
	
	@Column(name = "statistic")
	private String statistic;

	@Column(name = "application")
	private String application;

	@Column(name = "metric", tag = true)
	private String metric;

	@Column(name = "value")
	private Double value;

}
