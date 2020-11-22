package com.developerhelperhub.ms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.developerhelperhub.ms.config.MyCloudDefaultCloudServerConfiguration;

@EnableDiscoveryClient
@EnableScheduling
@EnableCircuitBreaker
@EnableConfigServer
@SpringBootApplication
@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		MyCloudDefaultCloudServerConfiguration.class }) })
public class MyCloudConfigApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MyCloudConfigApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
