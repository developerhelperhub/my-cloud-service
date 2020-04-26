package com.developerhelperhub.ms.id.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

	@RequestMapping(value = "/clients/applications", method = RequestMethod.GET)
	public String applications() {
		return "success";
	}

}
