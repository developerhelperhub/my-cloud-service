package com.developerhelperhub.ms.id.controller;

import com.netflix.discovery.shared.Applications;

import lombok.Data;

@Data
public class DetailModel {

	private int totalApplications;
	private int totalInstances;
	private Applications applications;
	
}
