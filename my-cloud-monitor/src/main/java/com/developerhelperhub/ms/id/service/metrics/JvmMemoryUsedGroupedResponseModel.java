package com.developerhelperhub.ms.id.service.metrics;

import java.util.List;

import lombok.Data;

@Data
public class JvmMemoryUsedGroupedResponseModel {

	private String application;

	private List<JvmMemoryUsedDataModel> data;

	@Data
	public static class JvmMemoryUsedDataModel {

		private String time;

		private String statistic;

		private String metric;

		private Double value;

	}

}
