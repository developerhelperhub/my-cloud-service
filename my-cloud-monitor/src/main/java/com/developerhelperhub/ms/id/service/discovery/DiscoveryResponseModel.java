package com.developerhelperhub.ms.id.service.discovery;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
public class DiscoveryResponseModel {

	@Data
	@Document
	public static class LeaseInfo {
		public int renewalIntervalInSecs;
		public int durationInSecs;
		public Object registrationTimestamp;
		public Object lastRenewalTimestamp;
		public int evictionTimestamp;
		public Object serviceUpTimestamp;
	}

	@Data
	@Document
	public static class Instance {
		public String instanceId;
		public String app;
		public String appGroupName;
		public String ipAddr;
		public String sid;
		public String homePageUrl;
		public String statusPageUrl;
		public String healthCheckUrl;
		public String secureHealthCheckUrl;
		public String vipAddress;
		public String secureVipAddress;
		public int countryId;
		public String hostName;
		public String status;
		public String overriddenStatus;
		public LeaseInfo leaseInfo;
		public boolean isCoordinatingDiscoveryServer;
		public Object lastUpdatedTimestamp;
		public Object lastDirtyTimestamp;
		public String actionType;
		public String asgName;
	}

	@Data
	public static class Application {
		public String name;
		public List<Instance> instance;
	}

	public List<Application> application;
}
