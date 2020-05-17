package com.developerhelperhub.ms.id.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.service.entity.JvmMemoryUsedEntity;
import com.developerhelperhub.ms.id.service.metrics.JvmMemoryUsedResponseModel;

import reactor.core.publisher.Flux;

@Service
public class MonitorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);

	@Autowired
	private InfluxDB influxDB;

	public List<JvmMemoryUsedResponseModel> getJvmMemoryUsed() {
		LOGGER.info("getJvmMemoryUsed ............");

		Query query = new Query("Select * from memory", "mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<JvmMemoryUsedEntity> memoryPointList = resultMapper.toPOJO(queryResult, JvmMemoryUsedEntity.class);

		influxDB.close();

		return memoryPointList.stream().map(d -> {

			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdfDate.format(new Date(d.getTime().toEpochMilli()));

			return new JvmMemoryUsedResponseModel(date, d.getStatistic(), d.getValue());

		}).collect(Collectors.toList());
	}

	public Flux<List<JvmMemoryUsedResponseModel>> streamJvmMemoryUsed() {
		List<JvmMemoryUsedResponseModel> memoryPointList = getJvmMemoryUsed();
		return Flux.just(memoryPointList);
	}

}
