package com.developerhelperhub.ms.monitor.actuator.health;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.monitor.application.ApplicationDiskSpace;

import lombok.Getter;
import lombok.Setter;

@Document("health")
@Getter
@Setter
public class HealthEntity {

	@Id
	private String name;

	private String hostName;

	private String application;

	private Long lastUpdated;

	private String status;

	private ApplicationDiskSpace diskSpace;

	public HealthEntity() {
	}

	public HealthEntity(String name) {
		this.name = name;
	}

}
