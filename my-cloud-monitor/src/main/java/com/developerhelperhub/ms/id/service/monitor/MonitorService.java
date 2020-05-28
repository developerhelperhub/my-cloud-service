package com.developerhelperhub.ms.id.service.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;
import com.developerhelperhub.ms.id.service.entity.MemoryEntity;
import com.developerhelperhub.ms.id.service.entity.ThreadEntity;
import com.developerhelperhub.ms.id.service.monitor.ApplicationMonitorModel.Matric;
import com.developerhelperhub.ms.id.service.monitor.ApplicationMonitorModel.MatricGroup;

import reactor.core.publisher.Flux;

@Service
public class MonitorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);

	public static final String MATRIX_JVM_MEMORY_USED = "jvm.memory.used";
	public static final String MATRIX_JVM_MEMORY_MAX = "jvm.memory.max";
	public static final String MATRIX_JVM_MEMORY_COMMITED = "jvm.memory.committed";

	public static final String MATRIX_JVM_BUFFER_MEMORY_PROMPTED = "jvm.buffer.memory.used";
	public static final String MATRIX_JVM_BUFFER_COUNT = "jvm.buffer.count";
	public static final String MATRIX_JVM_BUFFER_TOTAL_CAPACITY = "jvm.buffer.total.capacity";

	public static final String MATRIX_JVM_GC_MEMORY_ALLOCATED = "jvm.gc.memory.allocated";
	public static final String MATRIX_JVM_GC_MEMORY_PROMPTED = "jvm.gc.memory.promoted";

	public static final String MATRIX_JVM_THREADS_DAEMON = "jvm.threads.daemon";
	public static final String MATRIX_JVM_THREADS_LIVE = "jvm.threads.live";
	public static final String MATRIX_JVM_THREADS_PEAK = "jvm.threads.peak";

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

		if (applicationEntity.getInstance() != null) {
			model.setInstance(new ArrayList<>(applicationEntity.getInstance()));
		} else {
			model.setInstance(new ArrayList<>());
		}

		model.setLastUpdated(formatter.format(new Date(applicationEntity.getLastUpdated())));
		model.setName(applicationEntity.getName());
		model.setStatus(applicationEntity.getStatus());

		model.setMemory(getMemory(application).get("memory"));
		model.setBuffer(getMemory(application).get("buffer"));
		model.setThread(getThreads(application));

		influxDB.close();

		return model;
	}

	public Map<String, List<MatricGroup>> getMemory(String application) {

		LOGGER.info("getMemory ............");

		Query query = new Query(
				"select metric, time, value from memory WHERE application='" + application + "' AND time > 23h",
				"mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

		List<MemoryEntity> memoryPointList = resultMapper.toPOJO(queryResult, MemoryEntity.class);

		Map<String, List<MemoryEntity>> grouped = memoryPointList.stream()
				.collect(Collectors.groupingBy(MemoryEntity::getMetric));

		List<MatricGroup> memory = new ArrayList<>();
		List<MatricGroup> buffer = new ArrayList<>();

		for (Map.Entry<String, List<MemoryEntity>> key : grouped.entrySet()) {

			MatricGroup group = new MatricGroup();

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

			if (key.getKey().compareTo(MATRIX_JVM_MEMORY_USED) == 0
					|| key.getKey().compareTo(MATRIX_JVM_MEMORY_MAX) == 0) {

				group.setOrder((key.getKey().compareTo(MATRIX_JVM_MEMORY_MAX) == 0) ? 1 : 2);

				if (key.getKey().compareTo(MATRIX_JVM_MEMORY_USED) == 0) {
					group.setDisplay("Used");
				} else if (key.getKey().compareTo(MATRIX_JVM_MEMORY_MAX) == 0) {
					group.setDisplay("Max");
				} else {
					group.setDisplay(key.getKey());
				}

				memory.add(group);

			} else if (key.getKey().compareTo(MATRIX_JVM_BUFFER_MEMORY_PROMPTED) == 0
					|| key.getKey().compareTo(MATRIX_JVM_BUFFER_TOTAL_CAPACITY) == 0) {

				group.setOrder((key.getKey().compareTo(MATRIX_JVM_BUFFER_TOTAL_CAPACITY) == 0) ? 1 : 2);

				if (key.getKey().compareTo(MATRIX_JVM_BUFFER_MEMORY_PROMPTED) == 0) {
					group.setDisplay("Prompted");
				} else if (key.getKey().compareTo(MATRIX_JVM_BUFFER_TOTAL_CAPACITY) == 0) {
					group.setDisplay("Total Capacity");
				} else {
					group.setDisplay(key.getKey());
				}

				buffer.add(group);
			}

		}

		Collections.sort(memory);
		Collections.sort(buffer);

		Map<String, List<MatricGroup>> data = new HashMap<String, List<MatricGroup>>();

		data.put("memory", memory);
		data.put("buffer", buffer);

		return data;
	}

	public List<MatricGroup> getThreads(String application) {

		LOGGER.info("getThreads ............");

		Query query = new Query(
				"select metric, time, value from thread WHERE application='" + application + "' AND time > 23h",
				"mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

		List<ThreadEntity> memoryPointList = resultMapper.toPOJO(queryResult, ThreadEntity.class);

		Map<String, List<ThreadEntity>> grouped = memoryPointList.stream()
				.collect(Collectors.groupingBy(ThreadEntity::getMetric));

		List<MatricGroup> thread = new ArrayList<>();

		for (Map.Entry<String, List<ThreadEntity>> key : grouped.entrySet()) {

			MatricGroup group = new MatricGroup();

			group.setName(key.getKey());
			group.setMatrics(new ArrayList<>());

			for (ThreadEntity entity : key.getValue()) {

				Matric matric = new Matric();
				matric.setTimestamp(entity.getTime().toEpochMilli());
				matric.setTime(formatter.format(new Date(entity.getTime().toEpochMilli())));
				matric.setValue(entity.getValue().longValue());

				group.getMatrics().add(matric);

				Collections.sort(group.getMatrics());
			}

			if (key.getKey().compareTo(MATRIX_JVM_THREADS_PEAK) == 0) {
				group.setOrder(1);
				group.setDisplay("Peak");
			} else if (key.getKey().compareTo(MATRIX_JVM_THREADS_LIVE) == 0) {
				group.setOrder(2);
				group.setDisplay("Live");
			} else {
				group.setOrder(3);
				group.setDisplay("Daemon");
			}

			thread.add(group);

		}

		Collections.sort(thread);

		return thread;
	}

	public Flux<ApplicationMonitorModel> streamApplication(String application) {
		return Flux.just(getApplication(application));
	}
}
