package com.developerhelperhub.ms.id.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContext;

@RestController
public class ClientController {

	@Autowired
	private EurekaServerContext discoveryClient;

	@RequestMapping(value = "/clients/applications", method = RequestMethod.GET)
	public Applications applications() {
		return discoveryClient.getRegistry().getApplications();
	}

}
