package com.developerhelperhub.ms.id.monitor;

import org.influxdb.InfluxDB;

import com.developerhelperhub.ms.id.service.JmxService.JmxConnection;
import com.developerhelperhub.ms.id.service.application.ApplicationEntity;

import lombok.Data;

@Data
public abstract class ActuatorJmxMonitor {

	private String mBeanName;
	private String operation;
	private Object[] args;
	private String[] signatures;
	private JmxConnection connection;
	private ApplicationEntity application;
	private InfluxDB influxDB;

	public ActuatorJmxMonitor(String mBeanName, String operation) {
		this.mBeanName = mBeanName;
		this.operation = operation;
		this.args = null;
		this.signatures = null;
	}

	public ActuatorJmxMonitor(String mBeanName, String operation, Object[] args, String[] signatures) {
		this.mBeanName = mBeanName;
		this.operation = operation;
		this.args = args;
		this.signatures = signatures;
	}

	abstract public void process(JmxConnection connection);

}
