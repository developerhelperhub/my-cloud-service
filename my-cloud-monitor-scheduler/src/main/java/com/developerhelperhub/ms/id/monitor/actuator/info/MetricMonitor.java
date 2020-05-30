package com.developerhelperhub.ms.id.monitor.actuator.info;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developerhelperhub.ms.id.monitor.actuator.ActuatorJmxMonitor;
import com.developerhelperhub.ms.id.service.metrics.MatricModel;
import com.fasterxml.jackson.core.type.TypeReference;

public class MetricMonitor extends ActuatorJmxMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricMonitor.class);

	public MetricMonitor(String mBeanName, String operation, Object[] args, String[] signatures, String measurement) {
		super(mBeanName, operation, args, signatures, measurement);
	}

	@Override
	public void process() {

		String application = getConnection().getApplication().getName();
		String instanceId = getConnection().getInstanceId();

		try {

			MatricModel body = getConnection().read(getMBeanName(), getOperation(), getArgs(), getSignatures(),
					new TypeReference<MatricModel>() {
					});

			LOGGER.debug("Matric of {} : {}", instanceId, body.toString());

			Point.Builder builder = Point.measurement(getMeasurement())
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("metric", getOperation())
					.addField("instance_id", instanceId).addField("application", application)
					.addField("baseUnit", body.getBaseUnit());

			for (MatricModel.Measurement measurement : body.getMeasurements()) {

				builder.addField("statistic", measurement.getStatistic()).addField("value", measurement.getValue());

			}

			getInfluxDB().write(builder.build());

			LOGGER.debug("Matrics inserted value into {} {} {} !", getMeasurement(), application, getOperation());

			getInfluxDB().close();

		} catch (Exception e) {

			LOGGER.debug("{} JMX connection error :- {} ", instanceId, e.getMessage());

		}
	}

}
