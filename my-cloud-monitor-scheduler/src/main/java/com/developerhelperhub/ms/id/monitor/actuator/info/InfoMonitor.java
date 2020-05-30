package com.developerhelperhub.ms.id.monitor.actuator.info;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developerhelperhub.ms.id.monitor.actuator.ActuatorJmxMonitor;
import com.developerhelperhub.ms.id.service.info.ApplicationInfo;
import com.fasterxml.jackson.core.type.TypeReference;

public class InfoMonitor extends ActuatorJmxMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfoMonitor.class);

	private final String measurementName = "info";

	public InfoMonitor(String mBeanName, String operation) {
		super(mBeanName, operation);
	}

	@Override
	public void process() {

//		try {
//
//			ApplicationInfo body = getConnection().read(getMBeanName(), getOperation(),
//					new TypeReference<ApplicationInfo>() {
//					});
//
//			InfoEntity entity = getDataService().getInfo(getConnection().getApplication());
//
//			Point.Builder builder = Point.measurement(measurementName)
//					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
//					.addField("application", getConnection().getApplication())
//					.addField("build_version", body.getBuild().getVersion())
//					.addField("build_artifact", body.getBuild().getArtifact())
//					.addField("build_artifact", body.getBuild().getArtifact())
//					.addField("build_name", body.getBuild().getArtifact())
//					.addField("build_time", body.getBuild().getArtifact())
//					.addField("build_group", body.getBuild().getArtifact());
//
//			getInfluxDB().write(builder.build());
//
//			LOGGER.debug("Info inserted value into {} {} !", measurementName, getConnection().getApplication());
//
//			getInfluxDB().close();
//
//			entity.setBuild(body.getBuild());
//
//			getDataService().update(entity);
//
//		} catch (Exception e) {
//
//			LOGGER.debug("{} JMX connection error :- {} ", getConnection().getInstanceId(), e.getMessage());
//
//		}
	}

}
