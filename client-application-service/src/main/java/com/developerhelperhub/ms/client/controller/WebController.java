package com.developerhelperhub.ms.client.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

	@RequestMapping("/securedPage")
	public String securedPage(Model model, Principal principal) {
		
		model.addAttribute("authenticationName", principal.getName());
        
		return "securedPage";
	}

	@RequestMapping("/")
	public String index(Model model, Principal principal) {
		return "index";
	}
}
