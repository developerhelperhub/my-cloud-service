package com.developerhelperhub.ms.id.service.application;

import org.springframework.data.annotation.Id;

import com.developerhelperhub.ms.id.service.monitor.ApplicationMonitorModel;

import lombok.Data;

@Data
public class ApplicationModel {

	@Id
	private String name;

	private String status;

	private String lastUpdated;

	private ApplicationMonitorModel.Build build;

}
