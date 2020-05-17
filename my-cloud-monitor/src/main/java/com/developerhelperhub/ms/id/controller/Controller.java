package com.developerhelperhub.ms.id.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.service.MonitorService;
import com.developerhelperhub.ms.id.service.metrics.JvmMemoryUsedGroupedResponseModel;
import com.developerhelperhub.ms.id.service.metrics.JvmMemoryUsedResponseModel;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/monitor")
public class Controller {

	@Autowired
	private MonitorService service;

	@GetMapping(value = "/jvm-memory-used/stream-all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<List<JvmMemoryUsedResponseModel>> jvmMemoryUsedStream() {
		return service.streamJvmMemoryUsed();
	}

	@GetMapping(value = "/jvm-memory-used/stream-grouped", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<List<JvmMemoryUsedGroupedResponseModel>> streamJvmMemoryUsedGrouped() {
		return service.streamJvmMemoryUsedGrouped();
	}

	@GetMapping(value = "/jvm-memory-used")
	public List<JvmMemoryUsedGroupedResponseModel> getJvmMemoryUsed() {
		return service.getJvmMemoryUsedGrouped();
	}

}
