package com.developerhelperhub.ms.id.service.entity;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.Data;

@Data
@Measurement(name = "memory")
public class JvmMemoryUsedEntity {

	@Column(name = "time")
	private Instant time;

	@Column(name = "statistic")
	private String statistic;

	@Column(name = "value")
	private Double value;
}
