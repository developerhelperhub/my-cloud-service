package com.developerhelperhub.ms.id.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class Controller {

	@RequestMapping(value = "/applications", method = RequestMethod.GET)
	public String applications() {
		return "success";
	}

}
