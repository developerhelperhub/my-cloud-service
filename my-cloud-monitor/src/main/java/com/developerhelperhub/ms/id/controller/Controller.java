package com.developerhelperhub.ms.id.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.service.application.ApplicationEntity;
import com.developerhelperhub.ms.id.service.application.ApplicationModel;
import com.developerhelperhub.ms.id.service.application.MonitorApplication;
import com.developerhelperhub.ms.id.service.monitor.ApplicationMonitorModel;
import com.developerhelperhub.ms.id.service.monitor.MonitorService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/monitor")
public class Controller {

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private MonitorApplication application;

	@GetMapping(value = "/applications/all")
	public List<ApplicationEntity> getApplications() {
		return application.get();
	}

	@GetMapping(value = "/applications/basic-info")
	public List<ApplicationModel> getApplicationsBasicInfo() {
		return application.getBasicInfo();
	}

	@GetMapping(value = "/stream/applications/basic-info", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<List<ApplicationModel>> streamApplicationsBasicInfo() {
		return application.streamBasicInfo();
	}

	@GetMapping(value = "/applications/{applicationId}")
	public ApplicationMonitorModel getApplications(
			@PathVariable(value = "applicationId", required = true) String application) {
		return monitorService.getApplication(application);
	}

	@GetMapping(value = "/stream/applications/{applicationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ApplicationMonitorModel> streamApplications(
			@PathVariable(value = "applicationId", required = true) String application) {
		return monitorService.streamApplication(application);
	}

}
