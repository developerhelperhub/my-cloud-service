package com.developerhelperhub.ms.id.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.entity.mongodb.ApplicationEntity;
import com.developerhelperhub.ms.id.model.monitor.ApplicationModel;
import com.developerhelperhub.ms.id.repository.ApplicationRepository;

import reactor.core.publisher.Flux;

@Service
public class MonitorApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorApplication.class);

	@Autowired
	private ApplicationRepository repository;

	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

	public void add(String application) {

		ApplicationEntity entity = repository.findById(application).orElse(new ApplicationEntity());

		entity.setName(application);

		entity.setLastUpdated(System.currentTimeMillis());

		repository.save(entity);

		LOGGER.debug("Application inserted {} ", application);
	}

	public List<ApplicationModel> get() {
		return repository.findAll().stream().map(entity -> {

			ApplicationModel model = new ApplicationModel();

			model.setName(entity.getName());
			model.setStatus(entity.getStatus());
			model.setTotalInstance(entity.getTotalInstance());
			model.setRunningInstance(entity.getRunningInstance());
			model.setLastUpdated(formatter.format(new Date(entity.getLastUpdated())));

			return model;

		}).collect(Collectors.toList());
	}

	public Flux<List<ApplicationModel>> stream() {
		return Flux.just(get());
	}

}
