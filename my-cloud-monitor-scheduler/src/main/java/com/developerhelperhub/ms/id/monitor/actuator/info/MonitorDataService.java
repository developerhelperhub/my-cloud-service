package com.developerhelperhub.ms.id.monitor.actuator.info;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.ApplicationRepository;

@Service
public class MonitorDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorDataService.class);

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private InfoRepository infoRepository;

	@Autowired
	private HealthRepository healthRepository;

	public List<HealthEntity> getHelths() {
		return healthRepository.findAll();
	}

	public void update(HealthEntity entity) {

		ApplicationEntity applicationEntity = getApplication(entity.getName());
		applicationEntity.setStatus(entity.getStatus());

		update(applicationEntity);

		entity.setLastUpdated(System.currentTimeMillis());

		healthRepository.save(entity);

		LOGGER.debug("Updated health {} ", entity.getName());
	}
	
	public HealthEntity getHealth(String name) {
		LOGGER.debug("Loading info {} ", name);

		return healthRepository.findById(name).orElse(new HealthEntity(name));
	}

	public List<InfoEntity> getInfos() {
		return infoRepository.findAll();
	}

	public void update(InfoEntity entity) {

		ApplicationEntity applicationEntity = getApplication(entity.getName());
		applicationEntity.setBuild(entity.getBuild());

		update(applicationEntity);

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

	public void update(ApplicationEntity entity) {

		entity.setLastUpdated(System.currentTimeMillis());

		applicationRepository.save(entity);

		LOGGER.debug("Updated application {} ", entity.getName());
	}

	public ApplicationEntity getApplication(String name) {
		LOGGER.debug("Loading application {} ", name);

		return applicationRepository.findById(name).orElse(new ApplicationEntity(name));
	}

}
