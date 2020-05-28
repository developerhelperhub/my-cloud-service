package com.developerhelperhub.ms.id.service.application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.developerhelperhub.ms.id.service.monitor.ApplicationMonitorModel;

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

	public List<ApplicationEntity> get() {
		return repository.findAll();
	}

	public List<ApplicationModel> getBasicInfo() {
		List<ApplicationEntity> list = get();

		return list.stream().map(entity -> {

			ApplicationModel model = new ApplicationModel();
			model.setLastUpdated(formatter.format(new Date(entity.getLastUpdated())));
			model.setName(entity.getName());

			ApplicationMonitorModel.Build build = new ApplicationMonitorModel.Build();

			if (entity.getBuild() != null) {

				build.setArtifact(entity.getBuild().getArtifact());
				build.setGroup(entity.getBuild().getGroup());
				build.setName(entity.getBuild().getName());
				build.setTime(formatter.format(entity.getBuild().getTime()));
				build.setVersion(entity.getBuild().getVersion());

				model.setStatus(entity.getStatus());

			} else {
				build.setArtifact("-");
				build.setGroup("-");
				build.setName("-");
				build.setTime("-");
				build.setVersion("-");

				model.setStatus("N/R");
			}

			model.setBuild(build);

			return model;
		}).collect(Collectors.toList());
	}

	public Flux<List<ApplicationModel>> streamBasicInfo() {
		return Flux.just(getBasicInfo());
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
