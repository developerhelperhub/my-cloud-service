package com.developerhelperhub.ms.id.service.metrics;

import lombok.Data;

@Data
public class MemoryResponseModel {

	private String time;

	private String statistic;

	private String application;

	private String metric;

	private Double value;

	public MemoryResponseModel() {
	}

	public MemoryResponseModel(String time, String statistic, Double value, String application, String metric) {
		super();
		this.time = time;
		this.statistic = statistic;
		this.value = value;
		this.application = application;
		this.metric = metric;
	}

	@Override
	public String toString() {
		return "JvmMemoryUsedResponseModel [time=" + time + ", statistic=" + statistic + ", application=" + application
				+ ", metric=" + metric + ", value=" + value + "]";
	}

}
