package com.developerhelperhub.ms.monitor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.monitor.actuator.health.HealthEntity;
import com.developerhelperhub.ms.monitor.actuator.health.HealthRepository;
import com.developerhelperhub.ms.monitor.actuator.info.InfoEntity;
import com.developerhelperhub.ms.monitor.actuator.info.InfoRepository;
import com.developerhelperhub.ms.monitor.application.ApplicationEntity;
import com.developerhelperhub.ms.monitor.application.ApplicationRepository;
import com.developerhelperhub.ms.monitor.application.InstanceEntity;
import com.developerhelperhub.ms.monitor.application.InstanceRepository;

@Service
public class MonitorDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorDataService.class);

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private InfoRepository infoRepository;

	@Autowired
	private HealthRepository healthRepository;

	@Autowired
	private InstanceRepository instanceRepository;

	public List<HealthEntity> getHelths() {
		return healthRepository.findAll();
	}

	public void update(HealthEntity entity) {

		entity.setLastUpdated(System.currentTimeMillis());

		healthRepository.save(entity);

		LOGGER.debug("Updated health {}: {} ", entity.getName(), entity.getStatus());
	}

	public HealthEntity getHealth(String name) {
		LOGGER.debug("Loading info {} ", name);

		return healthRepository.findById(name).orElse(new HealthEntity(name));
	}

	public List<InfoEntity> getInfos() {
		return infoRepository.findAll();
	}

	public void update(InfoEntity entity) {

		entity.setLastUpdated(System.currentTimeMillis());

		infoRepository.save(entity);

		LOGGER.debug("Updated info {} ", entity.getName());
	}

	public InfoEntity getInfo(String name) {
		LOGGER.debug("Loading info {} ", name);

		return infoRepository.findById(name).orElse(new InfoEntity(name));
	}

	public List<ApplicationEntity> getApplications() {
		return applicationRepository.findAll();
	}

	public void addApplication(String name) {
		ApplicationEntity entity = getApplication(name);

		entity.setLastUpdated(System.currentTimeMillis());

		applicationRepository.save(entity);

		LOGGER.debug("Updated application {}: {}", entity.getName(), entity.getStatus());
	}

	public void update(ApplicationEntity entity) {

		entity.setLastUpdated(System.currentTimeMillis());

		applicationRepository.save(entity);

		LOGGER.debug("Updated application {}: {}", entity.getName(), entity.getStatus());
	}

	public ApplicationEntity getApplication(String name) {
		LOGGER.debug("Loading application {} ", name);

		return applicationRepository.findById(name).orElse(new ApplicationEntity(name));
	}

	public void update(InstanceEntity entity) {

		entity.setLastUpdated(System.currentTimeMillis());

		instanceRepository.save(entity);

		LOGGER.debug("Updated application {}: {}", entity.getInstanceId(), entity.getStatus());
	}

	public Map<String, InstanceEntity> getInstances(String app) {
		return instanceRepository.findByApplication(app).stream()
				.collect(Collectors.toMap(InstanceEntity::getInstanceId, entity -> entity));
	}

	public InstanceEntity getInstance(String instanceId) {
		LOGGER.debug("Loading instance {} ", instanceId);

		return instanceRepository.findById(instanceId)
				.orElse(new InstanceEntity(instanceId, UUID.randomUUID().toString()));
	}

}
