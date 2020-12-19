package com.developerhelperhub.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.model.monitor.AccessLogModel;
import com.developerhelperhub.ms.model.monitor.ApplicationInfoModel;
import com.developerhelperhub.ms.model.monitor.ApplicationInstanceInfoModel;
import com.developerhelperhub.ms.model.monitor.ApplicationInstanceModel;
import com.developerhelperhub.ms.model.monitor.ApplicationModel;
import com.developerhelperhub.ms.model.monitor.ElastiSearchLogModel;
import com.developerhelperhub.ms.model.monitor.LogMessageModel;
import com.developerhelperhub.ms.service.ElasticsearchService;
import com.developerhelperhub.ms.service.MonitorApplication;
import com.developerhelperhub.ms.service.MonitorService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/monitor")
public class Controller {

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private MonitorApplication application;

	@Autowired
	private ElasticsearchService elasticsearchService;

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

	@GetMapping(value = "/instances/{application}")
	public List<ApplicationInstanceModel> getInstances(
			@PathVariable(value = "application", required = true) String application) {
		return monitorService.getInstances(application);
	}

	@GetMapping(value = "/stream/instances/{application}")
	public Flux<List<ApplicationInstanceModel>> streamInstances(
			@PathVariable(value = "application", required = true) String application) {
		return monitorService.streamInstances(application);
	}

	@GetMapping(value = "/instances/{id}/info")
	public ApplicationInstanceInfoModel getInstanceInfo(@PathVariable(value = "id", required = true) String id) {
		return monitorService.getInstanceInfo(id);
	}

	@GetMapping(value = "/stream/instances/{id}/info")
	public Flux<ApplicationInstanceInfoModel> streamInstanceInfo(
			@PathVariable(value = "id", required = true) String id) {
		return monitorService.streamInstanceInfo(id);
	}

	@GetMapping(value = "/elasticsearch/search")
	public ElastiSearchLogModel getEasticsearch(@RequestParam("indexName") String indexName,
			@RequestParam("type") String type, @RequestParam("searchKey") String searchKey, @RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("order") String order, @RequestParam("fromDate") long fromDate,
			@RequestParam("toDate") long toDate) {
		return elasticsearchService.search(indexName, type, searchKey, page, size, order, fromDate, toDate);
	}

	@GetMapping(value = "/logs/search")
	public List<LogMessageModel> getEasticsearchLogs(@RequestParam("applicationId") String applicationId,
			@RequestParam("searchKey") String searchKey, @RequestParam("size") int size, @RequestParam("order") String order,
			@RequestParam("fromDate") long fromDate, @RequestParam("toDate") long toDate) {
		return elasticsearchService.searchLogs(applicationId, searchKey, size, order, fromDate, toDate);
	}

	@GetMapping(value = "/access-logs/search")
	public AccessLogModel getEasticsearchAccessLogs(@RequestParam("applicationId") String applicationId,
			@RequestParam("searchKey") String searchKey, @RequestParam("size") int size, @RequestParam("order") String order,
			@RequestParam("group") String group, @RequestParam("fromDate") long fromDate,
			@RequestParam("toDate") long toDate) {
		return elasticsearchService.searchAccessLogs(applicationId, searchKey, size, order, group, fromDate, toDate);
	}
}
