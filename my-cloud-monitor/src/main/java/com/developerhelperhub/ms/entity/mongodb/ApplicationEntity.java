package com.developerhelperhub.ms.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

	private int runningInstance;

	private int totalInstance;

	public ApplicationEntity() {
	}

	public ApplicationEntity(String name) {
		this.name = name;
	}

}
