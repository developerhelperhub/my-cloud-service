package com.developerhelperhub.ms.id.service;

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

import com.developerhelperhub.ms.id.entity.influxdb.DiskSpceEntity;
import com.developerhelperhub.ms.id.entity.influxdb.MemoryEntity;
import com.developerhelperhub.ms.id.entity.influxdb.ThreadEntity;
import com.developerhelperhub.ms.id.model.monitor.ApplicationDiskSpaceModel;
import com.developerhelperhub.ms.id.model.monitor.ApplicationInfoModel;
import com.developerhelperhub.ms.id.model.monitor.MatricGroupModel;
import com.developerhelperhub.ms.id.model.monitor.MatricModel;

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

	public Map<String, List<MatricGroupModel>> getMemory(String application) {

		LOGGER.info("getMemory ............");

		List<MatricGroupModel> memory = new ArrayList<>();
		List<MatricGroupModel> buffer = new ArrayList<>();

		Map<String, List<MatricGroupModel>> data = new HashMap<String, List<MatricGroupModel>>();

		data.put("memory", memory);
		data.put("buffer", buffer);

		if (application == null || application.isEmpty()) {
			return data;
		}

		Query query = new Query("select * from memory WHERE application='" + application
				+ "' and time > now() - 1h group by metric, host_name", "mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

		List<MemoryEntity> memoryPointList = resultMapper.toPOJO(queryResult, MemoryEntity.class);

		Map<String, List<MemoryEntity>> grouped = memoryPointList.stream()
				.collect(Collectors.groupingBy(MemoryEntity::getMetric));

		for (Map.Entry<String, List<MemoryEntity>> key : grouped.entrySet()) {

			MatricGroupModel group = new MatricGroupModel();

			group.setName(key.getKey());
			group.setMatrics(new ArrayList<>());

			for (MemoryEntity entity : key.getValue()) {

				MatricModel matric = new MatricModel();
				matric.setTimestamp(entity.getTime().toEpochMilli());
				matric.setTime(formatter.format(new Date(entity.getTime().toEpochMilli())));
				matric.setValue(entity.getValue() == null ? 0 : entity.getValue().longValue());

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

		return data;
	}

	public List<MatricGroupModel> getThreads(String application) {

		LOGGER.info("getThreads ............");

		List<MatricGroupModel> thread = new ArrayList<>();

		if (application == null || application.isEmpty()) {
			return thread;
		}

		Query query = new Query("select * from thread WHERE application='" + application
				+ "' and time > now() - 1h group by metric, host_name", "mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

		List<ThreadEntity> list = resultMapper.toPOJO(queryResult, ThreadEntity.class);

		Map<String, List<ThreadEntity>> grouped = list.stream().collect(Collectors.groupingBy(ThreadEntity::getMetric));

		for (Map.Entry<String, List<ThreadEntity>> key : grouped.entrySet()) {

			MatricGroupModel group = new MatricGroupModel();

			group.setName(key.getKey());
			group.setMatrics(new ArrayList<>());

			for (ThreadEntity entity : key.getValue()) {

				MatricModel matric = new MatricModel();
				matric.setTimestamp(entity.getTime().toEpochMilli());
				matric.setTime(formatter.format(new Date(entity.getTime().toEpochMilli())));
				matric.setValue(entity.getValue() == null ? 0 : entity.getValue().longValue());

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

	public ApplicationDiskSpaceModel getDiskSpace(String application) {

		LOGGER.info("Get disk space ............");

		ApplicationDiskSpaceModel out = new ApplicationDiskSpaceModel();

		List<ApplicationDiskSpaceModel.DataModel> free = new ArrayList<>();
		List<ApplicationDiskSpaceModel.DataModel> total = new ArrayList<>();
		List<ApplicationDiskSpaceModel.DataModel> threshold = new ArrayList<>();

		out.setFree(free);
		out.setThreshold(threshold);
		out.setTotal(total);

		if (application == null || application.isEmpty()) {
			return out;
		}

		Query query = new Query(
				"select * from health" + " where application='" + application
						+ "' AND disk_space_status='UP' and time > now() - 1h" + " group by host_name",
				"mycloudmonitordb");

		QueryResult queryResult = influxDB.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

		List<DiskSpceEntity> list = resultMapper.toPOJO(queryResult, DiskSpceEntity.class);

		for (DiskSpceEntity entity : list) {

			ApplicationDiskSpaceModel.DataModel freeData = new ApplicationDiskSpaceModel.DataModel();
			ApplicationDiskSpaceModel.DataModel totalData = new ApplicationDiskSpaceModel.DataModel();
			ApplicationDiskSpaceModel.DataModel thresholdData = new ApplicationDiskSpaceModel.DataModel();

			freeData.setTime(formatter.format(new Date(entity.getTime().toEpochMilli())));
			freeData.setValue(entity.getFree() == null ? 0 : entity.getFree().longValue());

			totalData.setTime(freeData.getTime());
			totalData.setValue(entity.getTotal() == null ? 0 : entity.getTotal().longValue());

			thresholdData.setTime(freeData.getTime());
			thresholdData.setValue(entity.getThreshold() == null ? 0 : entity.getThreshold().longValue());

			free.add(freeData);
			threshold.add(thresholdData);
			total.add(totalData);
		}

		return out;
	}

	public ApplicationInfoModel getInfo(String application) {
		ApplicationInfoModel model = new ApplicationInfoModel();

		Map<String, List<MatricGroupModel>> data = getMemory(application);
		List<MatricGroupModel> threads = getThreads(application);
		ApplicationDiskSpaceModel diskSpace = getDiskSpace(application);

		model.setMemory(data.get("memory"));
		model.setBuffer(data.get("buffer"));
		model.setThread(threads);
		model.setDiskSpace(diskSpace);

		return model;
	}

	public Flux<ApplicationInfoModel> streamInfo(String application) {
		return Flux.just(getInfo(application));
	}
}
