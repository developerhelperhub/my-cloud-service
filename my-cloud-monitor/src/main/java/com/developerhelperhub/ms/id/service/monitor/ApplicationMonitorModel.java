package com.developerhelperhub.ms.id.service.monitor;

import java.util.Collection;

import com.developerhelperhub.ms.id.service.application.ApplicationDiskSpace;
import com.developerhelperhub.ms.id.service.discovery.DiscoveryResponseModel;
import com.developerhelperhub.ms.id.service.info.ApplicationInfo;

import lombok.Data;

@Data
public class ApplicationMonitorModel {

	private String name;

	private String status;

	private ApplicationDiskSpace diskSpace;

	private Long lastUpdated;

	private Collection<DiscoveryResponseModel.Instance> instance;

	private ApplicationInfo.Build build;
}
