package com.developerhelperhub.ms.id.service.application;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.id.service.info.ApplicationInfo;

import lombok.Getter;
import lombok.Setter;

@Document("application")
@Getter
@Setter
public class ApplicationEntity {

	@Id
	private String name;

	private String status;

	private Long lastUpdated;

	private ApplicationInfo.Build build;

	private int runningInstance;

	private int totalInstance;

	public ApplicationEntity() {
	}

	public ApplicationEntity(String name) {
		this.name = name;
	}

}
