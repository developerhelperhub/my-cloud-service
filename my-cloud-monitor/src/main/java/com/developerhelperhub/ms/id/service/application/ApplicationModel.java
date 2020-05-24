package com.developerhelperhub.ms.id.service.application;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.id.service.info.ApplicationInfo;
import com.developerhelperhub.ms.id.service.info.ApplicationInfo.Build;

import lombok.Data;

@Data
@Document("applications")
public class ApplicationModel {

	@Id
	private String name;

	private String status;

	private Long lastUpdated;

	private ApplicationInfo.Build build;

	public ApplicationModel(String name, String status, Long lastUpdated, Build build) {
		super();
		this.name = name;
		this.status = status;
		this.lastUpdated = lastUpdated;
		this.build = build;
	}

}
