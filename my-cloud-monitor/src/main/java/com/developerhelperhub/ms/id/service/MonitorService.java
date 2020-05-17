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

import com.developerhelperhub.ms.id.service.entity.JvmMemoryUsedEntity;
import com.developerhelperhub.ms.id.service.metrics.JvmMemoryUsedGroupedResponseModel;
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

			return new JvmMemoryUsedResponseModel(date, d.getStatistic(), d.getValue(), d.getApplication(),
					d.getMetric());

		}).collect(Collectors.toList());
	}

	public List<JvmMemoryUsedGroupedResponseModel> getJvmMemoryUsedGrouped() {
		LOGGER.info("getJvmMemoryUsedGrouped ............");

		Query query = new Query("Select * from memory", "mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<JvmMemoryUsedEntity> memoryPointList = resultMapper.toPOJO(queryResult, JvmMemoryUsedEntity.class);

		Map<String, List<JvmMemoryUsedEntity>> groupedData = memoryPointList.stream()
				.collect(Collectors.groupingBy(JvmMemoryUsedEntity::getApplication));

		List<JvmMemoryUsedGroupedResponseModel> list = new ArrayList<JvmMemoryUsedGroupedResponseModel>();

		for (String application : groupedData.keySet()) {

			List<JvmMemoryUsedGroupedResponseModel.JvmMemoryUsedDataModel> data = new ArrayList<>();

			for (JvmMemoryUsedEntity entity : groupedData.get(application)) {

				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdfDate.format(new Date(entity.getTime().toEpochMilli()));

				JvmMemoryUsedGroupedResponseModel.JvmMemoryUsedDataModel dataModel = new JvmMemoryUsedGroupedResponseModel.JvmMemoryUsedDataModel();

				dataModel.setMetric(entity.getMetric());
				dataModel.setTime(date);
				dataModel.setValue(entity.getValue());
				dataModel.setStatistic(entity.getStatistic());

				data.add(dataModel);

			}

			JvmMemoryUsedGroupedResponseModel model = new JvmMemoryUsedGroupedResponseModel();
			model.setApplication(application);
			model.setData(data);

			list.add(model);
		}

		influxDB.close();

		return list;
	}

	public Flux<List<JvmMemoryUsedResponseModel>> streamJvmMemoryUsed() {

		List<JvmMemoryUsedResponseModel> memoryPointList = getJvmMemoryUsed();

		return Flux.just(memoryPointList);
	}

	public Flux<List<JvmMemoryUsedGroupedResponseModel>> streamJvmMemoryUsedGrouped() {

		List<JvmMemoryUsedGroupedResponseModel> memoryPointList = getJvmMemoryUsedGrouped();

		return Flux.just(memoryPointList);
	}

}
