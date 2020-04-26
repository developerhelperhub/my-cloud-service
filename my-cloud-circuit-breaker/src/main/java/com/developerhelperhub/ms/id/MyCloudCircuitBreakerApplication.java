package com.developerhelperhub.ms.id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard
public class MyCloudCircuitBreakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCloudCircuitBreakerApplication.class, args);
	}

}
