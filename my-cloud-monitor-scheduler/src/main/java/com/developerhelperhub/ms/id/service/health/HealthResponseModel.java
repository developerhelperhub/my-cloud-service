package com.developerhelperhub.ms.id.service.health;

import java.util.Map;

import lombok.Data;

@Data
public class HealthResponseModel {

	@Data
	public static class DiskSpace {
		public String status;
		public Map<String, Long> details;

		@Override
		public String toString() {
			return "DiskSpace [status=" + status + ", details=" + details + "]";
		}

	}

	@Data
	public static class Hystrix {
		public String status;

		@Override
		public String toString() {
			return "Hystrix [status=" + status + "]";
		}

	}

	@Data
	public static class Ping {
		public String status;

		@Override
		public String toString() {
			return "Ping [status=" + status + "]";
		}

	}

	@Data
	public static class RefreshScope {
		public String status;

		@Override
		public String toString() {
			return "RefreshScope [status=" + status + "]";
		}

	}

	@Data
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
