package com.developerhelperhub.ms.id.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.service.entity.MemoryUsedEntity;
import com.developerhelperhub.ms.id.service.metrics.MatricsGroupedResponseModel;
import com.developerhelperhub.ms.id.service.metrics.MemoryResponseModel;

import reactor.core.publisher.Flux;

@Service
public class MonitorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);

	@Autowired
	private InfluxDB influxDB;

	public List<MemoryResponseModel> getMemory() {
		LOGGER.info("getJvmMemoryUsed ............");

		Query query = new Query("Select * from memory", "mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<MemoryUsedEntity> memoryPointList = resultMapper.toPOJO(queryResult, MemoryUsedEntity.class);

		influxDB.close();

		return memoryPointList.stream().map(d -> {

			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdfDate.format(new Date(d.getTime().toEpochMilli()));

			return new MemoryResponseModel(date, d.getStatistic(), d.getValue(), d.getApplication(),
					d.getMetric());

		}).collect(Collectors.toList());
	}

	public List<MatricsGroupedResponseModel> getMemoryGrouped() {
		LOGGER.info("getJvmMemoryUsedGrouped ............");

		Query query = new Query("Select * from memory", "mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<MemoryUsedEntity> memoryPointList = resultMapper.toPOJO(queryResult, MemoryUsedEntity.class);

		Map<String, List<MemoryUsedEntity>> groupedData = memoryPointList.stream()
				.collect(Collectors.groupingBy(MemoryUsedEntity::getApplication));

		List<MatricsGroupedResponseModel> list = new ArrayList<MatricsGroupedResponseModel>();

		for (String application : groupedData.keySet()) {

			List<MatricsGroupedResponseModel.MatricsDataModel> data = new ArrayList<>();

			for (MemoryUsedEntity entity : groupedData.get(application)) {

				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdfDate.format(new Date(entity.getTime().toEpochMilli()));

				MatricsGroupedResponseModel.MatricsDataModel dataModel = new MatricsGroupedResponseModel.MatricsDataModel();

				dataModel.setMetric(entity.getMetric());
				dataModel.setTime(date);
				dataModel.setValue(entity.getValue());
				dataModel.setStatistic(entity.getStatistic());

				data.add(dataModel);

			}

			MatricsGroupedResponseModel model = new MatricsGroupedResponseModel();
			model.setApplication(application);
			model.setData(data);

			list.add(model);
		}

		influxDB.close();

		return list;
	}

	public Flux<List<MemoryResponseModel>> streamJvmMemoryUsed() {

		List<MemoryResponseModel> memoryPointList = getMemory();

		return Flux.just(memoryPointList);
	}

	public Flux<List<MatricsGroupedResponseModel>> streamMemoryGrouped() {

		List<MatricsGroupedResponseModel> memoryPointList = getMemoryGrouped();

		return Flux.just(memoryPointList);
	}

}
