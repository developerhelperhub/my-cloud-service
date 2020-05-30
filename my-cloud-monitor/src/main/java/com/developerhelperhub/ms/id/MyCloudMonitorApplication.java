package com.developerhelperhub.ms.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.developerhelperhub.ms.id.service.MonitorApplication;

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

		application.add("my-cloud-monitor");
		application.add("my-cloud-monitor-scheduler");
		application.add("my-cloud-identity");
		application.add("my-cloud-discovery");
		application.add("my-cloud-api-gateway");
	}
}
