package com.developerhelperhub.ms.id.service.application;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.id.service.discovery.DiscoveryResponseModel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Document("applications")
@Getter
@Setter
public class ApplicationEntity {

	@Document
	@Data
	public static class ApplicationDiskSpace {

		private String status;

		private Long free;

		private Long total;

		private Long threshold;
	}

	@Id
	private String name;

	private String status;

	private ApplicationDiskSpace diskSpace;

	private Long lastUpdated;

	private List<DiscoveryResponseModel.Instance> instance;

}