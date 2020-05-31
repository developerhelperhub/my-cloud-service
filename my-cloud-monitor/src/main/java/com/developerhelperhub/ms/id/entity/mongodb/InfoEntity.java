package com.developerhelperhub.ms.id.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.id.model.ApplicationInfo;

import lombok.Getter;
import lombok.Setter;

@Document("info")
@Getter
@Setter
public class InfoEntity {

	@Id
	private String name;

	private String application;

	private Long lastUpdated;

	private ApplicationInfo.Build build;

	public InfoEntity() {
	}

	public InfoEntity(String name) {
		this.name = name;
	}

}
