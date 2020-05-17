package com.developerhelperhub.ms.id.service.entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.Data;

@Data
@Measurement(name = "memory")
public class JvmMemoryUsedEntity {

	@Column(name = "statistic")
	private String statistic;

	@Column(name = "value")
	private Double value;
}
