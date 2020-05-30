package com.developerhelperhub.ms.id.entity.influxdb;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.Data;

@Data
@Measurement(name = "memory")
public class MemoryEntity {

	@Column(name = "time")
	private Instant time;

	@Column(name = "statistic")
	private String statistic;

	@Column(name = "application")
	private String application;

	@Column(name = "metric")
	private String metric;

	@Column(name = "value")
	private Double value;

}
