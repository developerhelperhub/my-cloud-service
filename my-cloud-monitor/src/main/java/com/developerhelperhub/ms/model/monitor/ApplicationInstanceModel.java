package com.developerhelperhub.ms.model.monitor;

import lombok.Data;

@Data
public class ApplicationInstanceModel {

	private String instanceId;
	private String name;
	private String version;
	private String status;
	private String lastUpdated;
	private String identifier;
}
