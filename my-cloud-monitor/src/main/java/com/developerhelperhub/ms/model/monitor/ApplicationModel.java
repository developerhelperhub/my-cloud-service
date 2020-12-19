package com.developerhelperhub.ms.model.monitor;

import org.springframework.data.annotation.Id;

import com.developerhelperhub.ms.model.ApplicationMonitorModel.Build;

import lombok.Data;

@Data
public class ApplicationModel {

	@Id
	private String name;

	private String status;

	private String lastUpdated;

	private int runningInstance;

	private int totalInstance;

}
