package com.developerhelperhub.ms.id.monitor.actuator.info;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developerhelperhub.ms.id.monitor.actuator.ActuatorJmxMonitor;
import com.developerhelperhub.ms.id.monitor.model.ApplicationInfo;
import com.fasterxml.jackson.core.type.TypeReference;

public class InfoMonitor extends ActuatorJmxMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfoMonitor.class);

	public InfoMonitor(String mBeanName, String operation, String measurement) {
		super(mBeanName, operation, measurement);
	}

	@Override
	public void process() {

		try {
			String application = getConnection().getApplication().getName();
			String instanceId = getConnection().getInstanceId();

			ApplicationInfo body = getConnection().read(getMBeanName(), getOperation(),
					new TypeReference<ApplicationInfo>() {
					});

			InfoEntity entity = getDataService().getInfo(instanceId);

			entity.setApplication(application);

			Point.Builder builder = Point.measurement(getMeasurement())
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("instance_id", instanceId)
					.addField("application", application).addField("build_version", body.getBuild().getVersion())
					.addField("build_artifact", body.getBuild().getArtifact())
					.addField("build_artifact", body.getBuild().getArtifact())
					.addField("build_name", body.getBuild().getArtifact())
					.addField("build_time", body.getBuild().getArtifact())
					.addField("build_group", body.getBuild().getArtifact());

			getInfluxDB().write(builder.build());

			LOGGER.debug("Info inserted value into {} {} !", getMeasurement(), instanceId);

			getInfluxDB().close();

			entity.setBuild(body.getBuild());

			getDataService().update(entity);

		} catch (Exception e) {

			LOGGER.debug("{} JMX connection error :- {} ", getConnection().getInstanceId(), e.getMessage());

		}
	}

}
