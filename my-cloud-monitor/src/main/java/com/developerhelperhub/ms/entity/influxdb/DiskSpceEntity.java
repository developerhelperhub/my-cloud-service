package com.developerhelperhub.ms.entity.influxdb;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.Data;

@Data
@Measurement(name = "health")
public class DiskSpceEntity {

	@Column(name = "time")
	private Instant time;

	@Column(name = "host_name", tag = true)
	private String hostName;
	
	@Column(name = "disk_space_free")
	private Double free;

	@Column(name = "disk_space_threshold")
	private Double threshold;

	@Column(name = "disk_space_total")
	private Double total;

}
