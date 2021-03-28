package com.developerhelperhub.ms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.developerhelperhub.ms.service.MonitorApplication;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCircuitBreaker
public class MyCloudMonitorApplication implements CommandLineRunner {

	@Autowired
	private MonitorApplication application;

	public static void main(String[] args) {
		SpringApplication.run(MyCloudMonitorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		application.addApplication("my-cloud-monitor");
		application.addApplication("my-cloud-monitor-scheduler");
		application.addApplication("my-cloud-identity");
		application.addApplication("my-cloud-discovery");
		application.addApplication("my-cloud-api-gateway");
	}
}
