package com.developerhelperhub.ms.id.model.monitor;

import java.util.List;
import java.util.Map;

import com.developerhelperhub.ms.id.model.ApplicationInfo;
import com.developerhelperhub.ms.id.model.DiscoveryResponseModel.LeaseInfo;

import lombok.Data;

@Data
public class ApplicationInstanceInfoModel {

	@Data
	public static class Instance {
		private String instanceId;
		private String application;
		private String appGroupName;
		private String ipAddr;
		private String sid;
		private String homePageUrl;
		private String statusPageUrl;
		private String healthCheckUrl;
		private String secureHealthCheckUrl;
		private String vipAddress;
		private String secureVipAddress;
		private int countryId;
		private String hostName;
		private String status;
		private String overriddenStatus;
		private LeaseInfo leaseInfo;
		private boolean isCoordinatingDiscoveryServer;
		private Object lastUpdatedTimestamp;
		private Object lastDirtyTimestamp;
		private String actionType;
		private String asgName;
		private Map<String, String> metadata;
	}
	
	private String id;
	private boolean found;
	private String application;
	private String status;
	private String lastUpdated;

	private ApplicationInfo.Build build;

	private Instance detail = new Instance();

	private ApplicationDiskSpaceModel diskSpace = new ApplicationDiskSpaceModel();

	private List<MatricGroupModel> memory;

	private List<MatricGroupModel> buffer;

	private List<MatricGroupModel> thread;

}
