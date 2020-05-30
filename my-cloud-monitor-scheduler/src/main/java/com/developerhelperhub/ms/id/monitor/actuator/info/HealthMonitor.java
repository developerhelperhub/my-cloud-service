package com.developerhelperhub.ms.id.monitor.actuator.info;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developerhelperhub.ms.id.monitor.actuator.ActuatorJmxMonitor;
import com.developerhelperhub.ms.id.service.application.ApplicationDiskSpace;
import com.developerhelperhub.ms.id.service.health.HealthResponseModel;
import com.fasterxml.jackson.core.type.TypeReference;

public class HealthMonitor extends ActuatorJmxMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(HealthMonitor.class);

	private final String STATUS_DOWN = "DOWN";

	public HealthMonitor(String mBeanName, String operation, String measurement) {
		super(mBeanName, operation, measurement);
	}

	@Override
	public void process() {

		String application = getConnection().getApplication().getName();
		String instanceId = getConnection().getInstanceId();

		HealthEntity entity = getDataService().getHealth(instanceId);

		LOGGER.debug("Current health status of {} : {}", instanceId, entity.getStatus());

		Point.Builder builder = Point.measurement(getMeasurement())
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("application", application)
				.addField("instance_id", instanceId);

		try {

			HealthResponseModel body = getConnection().read(getMBeanName(), getOperation(),
					new TypeReference<HealthResponseModel>() {
					});

			LOGGER.debug("Helath of {} : {}", instanceId, body.toString());

			builder.addField("status", body.getStatus()).addField("disk_space_status",
					body.getComponents().getDiskSpace().getStatus());

			entity.setStatus(body.getStatus());

			if (body.getComponents() != null) {

				if (body.getComponents().getDiskSpace() != null) {

					ApplicationDiskSpace diskSpace = new ApplicationDiskSpace();

					diskSpace.setFree(body.getComponents().getDiskSpace().getDetails().get("free"));
					diskSpace.setThreshold(body.getComponents().getDiskSpace().getDetails().get("threshold"));
					diskSpace.setTotal(body.getComponents().getDiskSpace().getDetails().get("total"));
					diskSpace.setStatus(body.getComponents().getDiskSpace().getStatus());

					builder.addField("disk_space_total", diskSpace.getTotal())
							.addField("disk_space_free", diskSpace.getFree())
							.addField("disk_space_threshold", diskSpace.getThreshold());

					entity.setDiskSpace(diskSpace);
				}

				if (body.getComponents().getHystrix() != null) {
					builder.addField("hystrix_status", body.getComponents().getHystrix().getStatus());
				}
			}

		} catch (Exception e) {

			LOGGER.debug("{} JMX connection error :- {} ", instanceId, e.getMessage());

			builder.addField("status", STATUS_DOWN);

			entity.setStatus(STATUS_DOWN);

		}

		getInfluxDB().write(builder.build());

		LOGGER.debug("Helath inserted value into {} {}: {}!", getMeasurement(), instanceId, entity.getStatus());

		getInfluxDB().close();

		getDataService().update(entity);
	}

}
