package com.developerhelperhub.ms.id.service.application;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.id.service.discovery.DiscoveryResponseModel;
import com.developerhelperhub.ms.id.service.info.ApplicationInfo;

import lombok.Getter;
import lombok.Setter;

@Document("applications")
@Getter
@Setter
public class ApplicationEntity {

	@Id
	private String name;

	private String status;

	private ApplicationDiskSpace diskSpace;

	private Long lastUpdated;

	private Collection<DiscoveryResponseModel.Instance> instance;

	private ApplicationInfo.Build build;

}
