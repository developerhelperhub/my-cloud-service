package com.developerhelperhub.ms.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.developerhelperhub.ms.id.service.MonitorJMXScheduler;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCircuitBreaker
public class MyCloudMonitorSchedulerApplication implements CommandLineRunner {

	@Autowired
	private MonitorJMXScheduler monitorJMXScheduler;

	public static void main(String[] args) {
		SpringApplication.run(MyCloudMonitorSchedulerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		monitorJMXScheduler.monitorInfo();
	}

}
