package com.developerhelperhub.ms.monitor.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthResponseModel {

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DiskSpace {
		public String status;
		public Map<String, Long> details;

		@Override
		public String toString() {
			return "DiskSpace [status=" + status + ", details=" + details + "]";
		}

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Hystrix {
		public String status;

		@Override
		public String toString() {
			return "Hystrix [status=" + status + "]";
		}

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Ping {
		public String status;

		@Override
		public String toString() {
			return "Ping [status=" + status + "]";
		}

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RefreshScope {
		public String status;

		@Override
		public String toString() {
			return "RefreshScope [status=" + status + "]";
		}

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class HealthComponent {
		public DiskSpace diskSpace;
		public Hystrix hystrix;
		public Ping ping;
		public RefreshScope refreshScope;

		@Override
		public String toString() {
			return "HealthComponent [diskSpace=" + diskSpace + ", hystrix=" + hystrix + ", ping=" + ping
					+ ", refreshScope=" + refreshScope + "]";
		}

	}

	public String status;
	public HealthComponent components;

	@Override
	public String toString() {
		return "HealthResponseModel [status=" + status + ", component=" + components + "]";
	}

}
