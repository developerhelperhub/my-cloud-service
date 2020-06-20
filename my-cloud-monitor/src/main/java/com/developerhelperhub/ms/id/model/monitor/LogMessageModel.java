package com.developerhelperhub.ms.id.model.monitor;

import lombok.Data;

@Data
public class LogMessageModel {

	private String datetime;
	private String threadName;
	private String logLevel;
	private String className;
	private String message;
}
