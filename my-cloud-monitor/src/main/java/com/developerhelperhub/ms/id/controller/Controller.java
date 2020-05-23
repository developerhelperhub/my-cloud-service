package com.developerhelperhub.ms.id.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.service.MonitorService;
import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;
import com.developerhelperhub.ms.id.service.metrics.MatricsGroupedResponseModel;
import com.developerhelperhub.ms.id.service.metrics.MemoryResponseModel;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/monitor")
public class Controller {

	@Autowired
	private MonitorService service;

	@Autowired
	private MonitorApplication application;

	@GetMapping(value = "/jvm-memory-used/stream-all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<List<MemoryResponseModel>> jvmMemoryUsedStream() {
		return service.streamJvmMemoryUsed();
	}

	@GetMapping(value = "/jvm-memory-used/stream-grouped", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<List<MatricsGroupedResponseModel>> streamJvmMemoryUsedGrouped() {
		return service.streamMemoryGrouped();
	}

	@GetMapping(value = "/jvm-memory-used")
	public List<MatricsGroupedResponseModel> getJvmMemoryUsed() {
		return service.getMemoryGrouped();
	}

	@GetMapping(value = "/applications")
	public List<ApplicationEntity> getApplications() {
		return application.get();
	}

}
