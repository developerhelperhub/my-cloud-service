package com.developerhelperhub.ms.id.monitor;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.developerhelperhub.ms.id.service.JmxService.JmxConnection;
import com.developerhelperhub.ms.id.service.info.ApplicationInfo;
import com.fasterxml.jackson.core.type.TypeReference;

public class InfoMonitor extends ActuatorJmxMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfoMonitor.class);

	private final String measurementName = "info";

	public InfoMonitor(String mBeanName, String operation) {
		super(mBeanName, operation);
	}

	@Override
	public void process(JmxConnection connection) {

		try {

			ApplicationInfo info = connection.read(getMBeanName(), getOperation(),
					new TypeReference<ApplicationInfo>() {
					});

			Point.Builder builder = Point.measurement(measurementName)
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("application", connection.getSerivce())
					.addField("build_version", body.getBuild().getVersion())
					.addField("build_artifact", body.getBuild().getArtifact())
					.addField("build_artifact", body.getBuild().getArtifact())
					.addField("build_name", body.getBuild().getArtifact())
					.addField("build_time", body.getBuild().getArtifact())
					.addField("build_group", body.getBuild().getArtifact());

			influxDB.write(builder.build());

			LOGGER.debug("Info inserted value into {} {} !", measurementName, application);

			influxDB.close();

			application.setBuild(body.getBuild());

			applicationSerivice.update(application);

		} catch (Exception e) {

			LOGGER.debug("{} JMX connection error :- {} ", connection.getInstanceId(), e.getMessage());

		}
	}

}
