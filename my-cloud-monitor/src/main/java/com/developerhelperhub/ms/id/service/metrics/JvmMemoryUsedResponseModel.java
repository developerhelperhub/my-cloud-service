package com.developerhelperhub.ms.id.service.metrics;

import lombok.Data;

@Data
public class JvmMemoryUsedResponseModel {

	private String time;

	private String statistic;

	private Double value;

	public JvmMemoryUsedResponseModel() {
	}

	public JvmMemoryUsedResponseModel(String time, String statistic, Double value) {
		super();
		this.time = time;
		this.statistic = statistic;
		this.value = value;
	}

}
