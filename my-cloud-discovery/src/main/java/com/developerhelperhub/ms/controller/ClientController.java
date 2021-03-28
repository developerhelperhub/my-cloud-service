package com.developerhelperhub.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContext;

@RestController
@RequestMapping(value = "/discovery")
public class ClientController {

	@Autowired
	private EurekaServerContext discoveryClient;

	@RequestMapping(value = "/applications", method = RequestMethod.GET)
	public Applications applications() {
		return discoveryClient.getRegistry().getApplications();
	}

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public DetailModel details() {
		DetailModel model = new DetailModel();

		SummaryModel summary = getSummary();

		model.setApplications(discoveryClient.getRegistry().getApplications());

		model.setTotalApplications(summary.getTotalApplications());
		model.setTotalInstances(summary.getTotalInstances());

		return model;
	}

	@RequestMapping(value = "/summary", method = RequestMethod.GET)
	public SummaryModel getSummary() {
		SummaryModel model = new SummaryModel();

		int totalApplications = 0;
		int totalInstances = 0;

		for (Application app : discoveryClient.getRegistry().getSortedApplications()) {

			totalApplications++;

			for (InstanceInfo inst : app.getInstances()) {
				totalInstances++;
			}
		}

		model.setTotalApplications(totalApplications);
		model.setTotalInstances(totalInstances);

		return model;
	}

}
