package com.developerhelperhub.ms.id.service.monitor;

import java.util.List;

import com.developerhelperhub.ms.id.service.application.ApplicationDiskSpace;
import com.developerhelperhub.ms.id.service.discovery.DiscoveryResponseModel;

import lombok.Data;

@Data
public class ApplicationMonitorModel {

	@Data
	public static class Build {
		private String version;
		private String artifact;
		private String name;
		private String group;
		private String time;

	}

	private String name;

	private String status;

	private ApplicationDiskSpace diskSpace;

	private String lastUpdated;

	private List<DiscoveryResponseModel.Instance> instance;

	private Build build;
}
