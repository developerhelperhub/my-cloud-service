package com.developerhelperhub.ms.id.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.developerhelperhub.ms.id.entity.mongodb.InfoEntity;
import com.developerhelperhub.ms.id.entity.mongodb.InstanceEntity;
import com.developerhelperhub.ms.id.model.ApplicationDiskSpace;
import com.developerhelperhub.ms.id.model.monitor.ApplicationDiskSpaceModel;
import com.developerhelperhub.ms.id.model.monitor.ApplicationInfoModel;
import com.developerhelperhub.ms.id.model.monitor.ApplicationInstanceInfoModel;
import com.developerhelperhub.ms.id.model.monitor.ApplicationInstanceModel;
import com.developerhelperhub.ms.id.model.monitor.MatricGroupModel;
import com.developerhelperhub.ms.id.model.monitor.MatricModel;
import com.developerhelperhub.ms.id.repository.HealthRepository;
import com.developerhelperhub.ms.id.repository.InfoRepository;
import com.developerhelperhub.ms.id.repository.InstanceRepository;

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
	private InstanceRepository instanceRepository;

	@Autowired
	private InfoRepository infoRepository;

	@Autowired
	private HealthRepository healthRepository;

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private class QueryData {

		public Map<String, List<MatricGroupModel>> getMemory(Query query) {

			LOGGER.info("getMemory ............");

			List<MatricGroupModel> memory = new ArrayList<>();
			List<MatricGroupModel> buffer = new ArrayList<>();

			Map<String, List<MatricGroupModel>> data = new HashMap<String, List<MatricGroupModel>>();

			data.put("memory", memory);
			data.put("buffer", buffer);

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

		public List<MatricGroupModel> getThreads(Query query) {

			LOGGER.info("getThreads ............");

			List<MatricGroupModel> thread = new ArrayList<>();

			QueryResult queryResult = influxDB.query(query);

			InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

			List<ThreadEntity> list = resultMapper.toPOJO(queryResult, ThreadEntity.class);

			Map<String, List<ThreadEntity>> grouped = list.stream()
					.collect(Collectors.groupingBy(ThreadEntity::getMetric));

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

		public ApplicationDiskSpaceModel getDiskSpace(Query query) {

			LOGGER.info("Get disk space ............");

			ApplicationDiskSpaceModel out = new ApplicationDiskSpaceModel();

			List<ApplicationDiskSpaceModel.DataModel> free = new ArrayList<>();
			List<ApplicationDiskSpaceModel.DataModel> total = new ArrayList<>();
			List<ApplicationDiskSpaceModel.DataModel> threshold = new ArrayList<>();

			out.setFree(free);
			out.setThreshold(threshold);
			out.setTotal(total);

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
	}

	public ApplicationInfoModel getInfo(String application) {
		ApplicationInfoModel model = new ApplicationInfoModel();

		QueryData queryData = new QueryData();

		Query queryMemory = new Query("select * from memory WHERE application='" + application
				+ "' and time > now() - 1h group by metric, host_name", "mycloudmonitordb");

		Query queryThread = new Query("select * from thread WHERE application='" + application
				+ "' and time > now() - 1h group by metric, host_name", "mycloudmonitordb");

		Query diskSpaceQuery = new Query(
				"select * from health" + " where application='" + application
						+ "' AND disk_space_status='UP' and time > now() - 1h" + " group by host_name",
				"mycloudmonitordb");

		Map<String, List<MatricGroupModel>> data = queryData.getMemory(queryMemory);
		List<MatricGroupModel> threads = queryData.getThreads(queryThread);
		ApplicationDiskSpaceModel diskSpace = queryData.getDiskSpace(diskSpaceQuery);

		model.setMemory(data.get("memory"));
		model.setBuffer(data.get("buffer"));
		model.setThread(threads);
		model.setDiskSpace(diskSpace);

		return model;
	}

	public Flux<ApplicationInfoModel> streamInfo(String application) {
		return Flux.just(getInfo(application));
	}

	public List<ApplicationInstanceModel> getInstances(String application) {
		List<ApplicationInstanceModel> list = new ArrayList<>();

		for (InstanceEntity instance : instanceRepository.findByApplication(application)) {
			ApplicationInstanceModel model = new ApplicationInstanceModel();
			model.setInstanceId(instance.getInstanceId());
			model.setStatus(instance.getStatus());
			model.setIdentifier(instance.getIdentifier());

			Optional<InfoEntity> info = infoRepository.findById(instance.getInstanceId());

			if (info.isPresent()) {
				model.setName(info.get().getBuild().getName());
				model.setVersion(info.get().getBuild().getVersion());
			} else {
				model.setName("-");
				model.setVersion("-");
			}

			model.setLastUpdated(formatter.format(new Date(instance.getLastUpdated())));

			list.add(model);
		}

		return list;
	}

	public Flux<List<ApplicationInstanceModel>> streamInstances(String application) {
		return Flux.just(getInstances(application));
	}

	public ApplicationInstanceInfoModel getInstanceInfo(String id) {
		ApplicationInstanceInfoModel model = new ApplicationInstanceInfoModel();

		Optional<InstanceEntity> entityOpt = instanceRepository.findByIdentifier(id);

		if (!entityOpt.isPresent()) {
			model.setFound(false);

			return model;
		}

		InstanceEntity instanceEntity = entityOpt.get();

		model.setFound(true);
		model.setId(id);
		model.setLastUpdated(formatter.format(new Date(instanceEntity.getLastUpdated())));
		model.setApplication(instanceEntity.getApplication());
		model.setStatus(instanceEntity.getStatus());

		model.getDetail().setInstanceId(instanceEntity.getInstanceId());
		model.getDetail().setApplication(instanceEntity.getApplication().toLowerCase());
		model.getDetail().setAppGroupName(instanceEntity.getAppGroupName());
		model.getDetail().setIpAddr(instanceEntity.getIpAddr());
		model.getDetail().setSid(instanceEntity.getSid());
		model.getDetail().setHomePageUrl(instanceEntity.getHomePageUrl());
		model.getDetail().setStatusPageUrl(instanceEntity.getStatusPageUrl());
		model.getDetail().setHealthCheckUrl(instanceEntity.getHealthCheckUrl());
		model.getDetail().setSecureHealthCheckUrl(instanceEntity.getSecureHealthCheckUrl());
		model.getDetail().setVipAddress(instanceEntity.getVipAddress());
		model.getDetail().setSecureVipAddress(instanceEntity.getSecureVipAddress());
		model.getDetail().setCountryId(instanceEntity.getCountryId());
		model.getDetail().setHostName(instanceEntity.getHostName());
		model.getDetail().setStatus(instanceEntity.getStatus());
		model.getDetail().setOverriddenStatus(instanceEntity.getOverriddenStatus());

		model.getDetail().getLeaseInfo().setDurationInSecs(instanceEntity.getLeaseInfo().getDurationInSecs());
		model.getDetail().getLeaseInfo().setEvictionTimestamp(instanceEntity.getLeaseInfo().getEvictionTimestamp());
		model.getDetail().getLeaseInfo().setLastRenewalTimestamp(
				formatter.format(new Date(instanceEntity.getLeaseInfo().getLastRenewalTimestamp())));
		model.getDetail().getLeaseInfo().setRegistrationTimestamp(
				formatter.format(new Date(instanceEntity.getLeaseInfo().getRegistrationTimestamp())));
		model.getDetail().getLeaseInfo()
				.setRenewalIntervalInSecs(instanceEntity.getLeaseInfo().getRenewalIntervalInSecs());
		model.getDetail().getLeaseInfo().setServiceUpTimestamp(
				formatter.format(new Date(instanceEntity.getLeaseInfo().getServiceUpTimestamp())));

		model.getDetail().setCoordinatingDiscoveryServer(instanceEntity.isCoordinatingDiscoveryServer());
		model.getDetail().setLastUpdatedTimestamp(formatter.format(new Date(instanceEntity.getLastUpdatedTimestamp())));
		model.getDetail().setLastDirtyTimestamp(formatter.format(new Date(instanceEntity.getLastDirtyTimestamp())));
		model.getDetail().setActionType(instanceEntity.getActionType());
		model.getDetail().setAsgName(instanceEntity.getAsgName());
		model.getDetail().setMetadata(instanceEntity.getMetadata());

		infoRepository.findById(instanceEntity.getInstanceId()).ifPresent(info -> {
			model.setBuild(info.getBuild());
		});

		QueryData queryData = new QueryData();

		Query diskSpaceQuery = new Query(
				"select * from health" + " where instance_id='" + instanceEntity.getInstanceId()
						+ "' AND disk_space_status='UP' and time > now() - 1h" + " group by host_name",
				"mycloudmonitordb");

		Query threadQuery = new Query("select * from thread WHERE instance_id='" + instanceEntity.getInstanceId()
				+ "' and time > now() - 1h group by metric, host_name", "mycloudmonitordb");

		Query memoryQuery = new Query("select * from memory WHERE instance_id='" + instanceEntity.getInstanceId()
				+ "' and time > now() - 1h group by metric, host_name", "mycloudmonitordb");

		model.setDiskSpace(queryData.getDiskSpace(diskSpaceQuery));

		healthRepository.findById(instanceEntity.getInstanceId()).ifPresent(entity -> {
			model.getDiskSpace().setData(entity.getDiskSpace());
		});

		if (model.getDiskSpace().getData() == null) {

			ApplicationDiskSpace data = new ApplicationDiskSpace();

			data.setFree(0L);
			data.setFree(0L);
			data.setTotal(0L);

			model.getDiskSpace().setData(data);
		}

		Map<String, List<MatricGroupModel>> memory = queryData.getMemory(memoryQuery);

		model.setMemory(memory.get("memory"));
		model.setBuffer(memory.get("buffer"));

		model.setThread(queryData.getThreads(threadQuery));

		return model;
	}

	public Flux<ApplicationInstanceInfoModel> streamInstanceInfo(String id) {
		return Flux.just(getInstanceInfo(id));
	}

}
