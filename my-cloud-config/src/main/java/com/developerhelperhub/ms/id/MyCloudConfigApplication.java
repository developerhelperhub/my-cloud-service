package com.developerhelperhub.ms.id;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCircuitBreaker
@EnableConfigServer
public class MyCloudConfigApplication implements CommandLineRunner {


	public static void main(String[] args) {
		SpringApplication.run(MyCloudConfigApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
