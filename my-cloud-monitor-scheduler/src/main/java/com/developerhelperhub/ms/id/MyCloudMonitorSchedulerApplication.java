package com.developerhelperhub.ms.id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCircuitBreaker
public class MyCloudMonitorSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCloudMonitorSchedulerApplication.class, args);
	}
}
