package com.developerhelperhub.ms.model.monitor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AccessLogModel {

	@Data
	public static class Group {

		private Long groupTime;
		private Long timestamp;

		@Override
		public int hashCode() {
			return groupTime.hashCode();
		}
	}

	@Data
	public static class AccessLogMessageModel {

		private Date datetime;
		private String datetimeFormatted;
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

	@Data
	public static class Metric {

		private String time;
		private int methodPost;
		private int methodPut;
		private int methodDelete;
		private int methodGet;
		private int methodPatch;
		private int methodOther;
		private int request;
		private int status1x;
		private int status2x;
		private int status3x;
		private int status4x;
		private int status5x;
		private int statusx;

	}

	private String group;
	private Collection<Metric> matrics;
	private List<AccessLogMessageModel> messages;
}
