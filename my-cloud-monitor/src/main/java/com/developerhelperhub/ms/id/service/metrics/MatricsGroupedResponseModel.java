package com.developerhelperhub.ms.id.service.metrics;

import java.util.List;

import lombok.Data;

@Data
public class MatricsGroupedResponseModel {

	private String application;

	private List<MatricsDataModel> data;

	@Data
	public static class MatricsDataModel {

		private String time;

		private String statistic;

		private String metric;

		private Double value;

	}

}
