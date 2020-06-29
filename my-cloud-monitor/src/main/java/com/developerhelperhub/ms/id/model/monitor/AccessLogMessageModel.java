package com.developerhelperhub.ms.id.model.monitor;

import lombok.Data;

@Data
public class AccessLogMessageModel {

	private String datetime;
	private String remoteIpAddress;
	private String remoteHostname;
	private String localPort;
	private String requestMethod;
	private String requestUrl;
	private String queryString;
	private String statusCode;
	private float timeProcessRequestMills;
	private float timeProcessRequestSeconds;
	private float timeCommitResponseMills;
	private String remoteLogicalUsername;
	private String userSessionId;
	private String currentThreadName;
	private String localServerName;
	private String authenticatedRemoteUsername;
}
