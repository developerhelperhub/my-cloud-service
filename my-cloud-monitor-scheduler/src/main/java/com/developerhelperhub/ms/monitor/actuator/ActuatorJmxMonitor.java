package com.developerhelperhub.ms.monitor.actuator;

import org.influxdb.InfluxDB;

import com.developerhelperhub.ms.monitor.MonitorDataService;
import com.developerhelperhub.ms.monitor.JmxService.JmxConnection;

import lombok.Data;

@Data
public abstract class ActuatorJmxMonitor {

	private String mBeanName;
	private String operation;
	private String[] args;
	private String measurement;
	private JmxConnection connection;
	private MonitorDataService dataService;
	private InfluxDB influxDB;

	public ActuatorJmxMonitor(String mBeanName, String operation, String measurement) {
		this.mBeanName = mBeanName;
		this.operation = operation;
		this.measurement = measurement;
		this.args = null;
	}

	public ActuatorJmxMonitor(String mBeanName, String operation, String[] args, String measurement) {
		this.mBeanName = mBeanName;
		this.operation = operation;
		this.args = args;
		this.measurement = measurement;
	}

	abstract public void process();

}
