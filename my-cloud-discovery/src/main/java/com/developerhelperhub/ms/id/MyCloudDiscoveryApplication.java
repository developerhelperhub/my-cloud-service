package com.developerhelperhub.ms.id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MyCloudDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCloudDiscoveryApplication.class, args);
	}

}
