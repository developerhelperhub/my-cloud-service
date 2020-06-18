package com.developerhelperhub.ms.id.monitor.actuator;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developerhelperhub.ms.id.monitor.model.MatricModel;
import com.fasterxml.jackson.core.type.TypeReference;

public class MetricMonitor extends ActuatorJmxMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricMonitor.class);

	public MetricMonitor(String mBeanName, String operation, String[] args, String measurement) {
		super(mBeanName, operation, args, measurement);
	}

	@Override
	public void process() {

		String application = getConnection().getApplication().getName();
		String instanceId = getConnection().getInstanceId();

		try {

			MatricModel body = getConnection().read(getMBeanName(), getOperation(), getArgs(),
					new TypeReference<MatricModel>() {
					});

			LOGGER.debug("Matric of {} : {}", instanceId, body.toString());

			Point.Builder builder = Point.measurement(getMeasurement()).tag("metric", (String) getArgs()[0])
					.tag("host_name", getConnection().getHostName()).tag("instance_id", instanceId)
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).addField("instance_id", instanceId)
					.addField("application", application).addField("baseUnit", body.getBaseUnit());

			for (MatricModel.Measurement measurement : body.getMeasurements()) {

				builder.addField("statistic", measurement.getStatistic()).addField("value", measurement.getValue());

			}

			getInfluxDB().write(builder.build());

			LOGGER.debug("Matrics inserted value into {} {} {} !", getMeasurement(), application, getOperation());

			getInfluxDB().close();

		} catch (Exception e) {

			e.printStackTrace();

			LOGGER.debug("{} JMX connection error :- {} ", instanceId, e.getMessage());

		}
	}

}
