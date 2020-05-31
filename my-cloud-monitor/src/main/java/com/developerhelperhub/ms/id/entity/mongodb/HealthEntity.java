package com.developerhelperhub.ms.id.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.id.model.ApplicationDiskSpace;

import lombok.Getter;
import lombok.Setter;

@Document("health")
@Getter
@Setter
public class HealthEntity {

	@Id
	private String name;

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
