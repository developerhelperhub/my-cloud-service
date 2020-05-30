package com.developerhelperhub.ms.id.monitor.actuator;

import org.influxdb.InfluxDB;

import com.developerhelperhub.ms.id.monitor.actuator.info.MonitorDataService;
import com.developerhelperhub.ms.id.service.JmxService.JmxConnection;

import lombok.Data;

@Data
public abstract class ActuatorJmxMonitor {

	private String mBeanName;
	private String operation;
	private Object[] args;
	private String[] signatures;
	private String measurement;
	private JmxConnection connection;
	private MonitorDataService dataService;
	private InfluxDB influxDB;

	public ActuatorJmxMonitor(String mBeanName, String operation, String measurement) {
		this.mBeanName = mBeanName;
		this.operation = operation;
		this.measurement = measurement;
		this.args = null;
		this.signatures = null;
	}

	public ActuatorJmxMonitor(String mBeanName, String operation, Object[] args, String[] signatures,
			String measurement) {
		this.mBeanName = mBeanName;
		this.operation = operation;
		this.args = args;
		this.signatures = signatures;
		this.measurement = measurement;
	}

	abstract public void process();

}
