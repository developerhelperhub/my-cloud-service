package com.developerhelperhub.ms.monitor.application;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.developerhelperhub.ms.monitor.model.DiscoveryResponseModel.LeaseInfo;

import lombok.Getter;
import lombok.Setter;

@Document("instance")
@Getter
@Setter
public class InstanceEntity {

	@Id
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
	private Long lastUpdated;
	private String identifier;

	public InstanceEntity() {
	}

	public InstanceEntity(String instanceId, String identifier) {
		this.instanceId = instanceId;
		this.identifier = identifier;
	}

}
