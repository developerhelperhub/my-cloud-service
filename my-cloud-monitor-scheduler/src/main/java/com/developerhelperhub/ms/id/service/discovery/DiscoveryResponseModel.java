package com.developerhelperhub.ms.id.service.discovery;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
public class DiscoveryResponseModel {

	@Data
	@Document
	public static class LeaseInfo {
		private int renewalIntervalInSecs;
		private int durationInSecs;
		private Object registrationTimestamp;
		private Object lastRenewalTimestamp;
		private int evictionTimestamp;
		private Object serviceUpTimestamp;
	}

	@Data
	@Document
	public static class Instance {

		private String instanceId;
		private String app;
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

	@Data
	public static class Application {
		public String name;
		public List<Instance> instance;
	}

	public List<Application> application;
}
