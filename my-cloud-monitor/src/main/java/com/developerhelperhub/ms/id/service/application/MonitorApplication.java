package com.developerhelperhub.ms.id.service.application;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class MonitorApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorApplication.class);

	@Autowired
	private ApplicationRepository repository;

	@Autowired
	private MongoTemplate template;

	public void add(String application) {

		ApplicationEntity entity = repository.findById(application).orElse(new ApplicationEntity());

		entity.setName(application);

		entity.setLastUpdated(System.currentTimeMillis());

		repository.save(entity);

		LOGGER.debug("Application inserted {} ", application);
	}

	public List<ApplicationEntity> get() {
		return repository.findAll();
	}

	public List<ApplicationModel> getBasicInfo() {
		return template.findAll(ApplicationModel.class);
	}

	public Flux<List<ApplicationModel>> streamBasicInfo() {
		return Flux.just(getBasicInfo());
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
