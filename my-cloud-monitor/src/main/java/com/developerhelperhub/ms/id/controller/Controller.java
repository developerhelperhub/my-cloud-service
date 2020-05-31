package com.developerhelperhub.ms.id.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.model.monitor.ApplicationInfoModel;
import com.developerhelperhub.ms.id.model.monitor.ApplicationModel;
import com.developerhelperhub.ms.id.service.MonitorApplication;
import com.developerhelperhub.ms.id.service.MonitorService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/monitor")
public class Controller {

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private MonitorApplication application;

	@GetMapping(value = "/applications/all")
	public List<ApplicationModel> getApplications() {
		return application.getApplication();
	}

	@GetMapping(value = "/stream/applications/all")
	public Flux<List<ApplicationModel>> streamApplications() {
		return application.streamApplication();
	}

	@GetMapping(value = "/applications/{application}/info")
	public ApplicationInfoModel getInfo(@PathVariable(value = "application", required = true) String application) {
		return monitorService.getInfo(application);
	}

	@GetMapping(value = "/stream/applications/{application}/info")
	public Flux<ApplicationInfoModel> streamInfo(
			@PathVariable(value = "application", required = true) String application) {
		return monitorService.streamInfo(application);
	}

}
