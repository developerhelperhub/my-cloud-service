package com.developerhelperhub.ms.id.service.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

import com.developerhelperhub.ms.id.service.MonitorScheduler;
import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;
import com.developerhelperhub.ms.id.service.entity.MemoryEntity;
import com.developerhelperhub.ms.id.service.monitor.ApplicationMonitorModel.Matric;
import com.developerhelperhub.ms.id.service.monitor.ApplicationMonitorModel.MatricGroup;

import reactor.core.publisher.Flux;

@Service
public class MonitorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);

	@Autowired
	private InfluxDB influxDB;

	@Autowired
	private MonitorApplication monitorApplication;

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ApplicationMonitorModel getApplication(String application) {
		ApplicationMonitorModel model = new ApplicationMonitorModel();

		ApplicationEntity applicationEntity = monitorApplication.get(application);

		ApplicationMonitorModel.Build build = new ApplicationMonitorModel.Build();
		build.setArtifact(applicationEntity.getBuild().getArtifact());
		build.setGroup(applicationEntity.getBuild().getGroup());
		build.setName(applicationEntity.getBuild().getName());
		build.setTime(formatter.format(applicationEntity.getBuild().getTime()));
		build.setVersion(applicationEntity.getBuild().getVersion());
		model.setBuild(build);

		model.setDiskSpace(applicationEntity.getDiskSpace());
		model.setInstance(new ArrayList<>(applicationEntity.getInstance()));
		model.setLastUpdated(formatter.format(new Date(applicationEntity.getLastUpdated())));
		model.setName(applicationEntity.getName());
		model.setStatus(applicationEntity.getStatus());

		model.setMemory(getMemory(application));

		return model;
	}

	public List<MatricGroup> getMemory(String application) {

		LOGGER.info("getMemory ............");

		Query query = new Query("select metric, time, value from memory WHERE application='" + application
				+ "' AND (metric='" + MonitorScheduler.MATRIX_JVM_MEMORY_MAX + "' OR metric='"
				+ MonitorScheduler.MATRIX_JVM_MEMORY_USED + "') AND time > 23h", "mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

		List<MemoryEntity> memoryPointList = resultMapper.toPOJO(queryResult, MemoryEntity.class);

		influxDB.close();

		Map<String, List<MemoryEntity>> grouped = memoryPointList.stream()
				.collect(Collectors.groupingBy(MemoryEntity::getMetric));

		List<MatricGroup> memory = new ArrayList<>();

		for (Map.Entry<String, List<MemoryEntity>> key : grouped.entrySet()) {

			MatricGroup group = new MatricGroup();

			group.setOrder((key.getKey().compareTo(MonitorScheduler.MATRIX_JVM_MEMORY_MAX) == 0) ? 1 : 2);
			group.setName(key.getKey());
			group.setMatrics(new ArrayList<>());

			for (MemoryEntity entity : key.getValue()) {

				Matric matric = new Matric();
				matric.setTimestamp(entity.getTime().toEpochMilli());
				matric.setTime(formatter.format(new Date(entity.getTime().toEpochMilli())));
				matric.setValue(entity.getValue().longValue());

				group.getMatrics().add(matric);

				Collections.sort(group.getMatrics());
			}

			memory.add(group);
		}

		Collections.sort(memory);

		return memory;
	}

	public Flux<ApplicationMonitorModel> streamApplication(String application) {
		return Flux.just(getApplication(application));
	}
}
