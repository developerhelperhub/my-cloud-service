package com.developerhelperhub.ms.id.model.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.id.model.ApplicationInfo;

import lombok.Data;

@Data
public class ApplicationInstanceInfoModel {

	@Data
	@Document
	public static class LeaseInfo {
		public int renewalIntervalInSecs;
		public int durationInSecs;
		public String registrationTimestamp;
		public String lastRenewalTimestamp;
		public int evictionTimestamp;
		public String serviceUpTimestamp;
	}

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
		private LeaseInfo leaseInfo = new LeaseInfo();
		private boolean isCoordinatingDiscoveryServer;
		private String lastUpdatedTimestamp;
		private String lastDirtyTimestamp;
		private String actionType;
		private String asgName;
		private List<Metadata> metadata = new ArrayList<>();
	}

	@Data
	public static class Metadata {
		private String key;
		private String value;

		public Metadata() {

		}

		public Metadata(String key, String value) {
			this.key = key;
			this.value = value;
		}
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
