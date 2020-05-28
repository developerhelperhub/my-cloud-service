package com.developerhelperhub.ms.id.service.application;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonitorApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorApplication.class);

	@Autowired
	private ApplicationRepository repository;

	public List<ApplicationEntity> get() {
		return repository.findAll();
	}

	public void update(ApplicationEntity entity) {

		entity.setLastUpdated(System.currentTimeMillis());

		repository.save(entity);

		LOGGER.debug("Updated application {} ", entity.getName());
	}

	public ApplicationEntity get(String name) {
		return repository.findById(name).orElseThrow(new Supplier<RuntimeException>() {
			@Override
			public RuntimeException get() {
				return new RuntimeException("Application not found!");
			}
		});
	}

}
