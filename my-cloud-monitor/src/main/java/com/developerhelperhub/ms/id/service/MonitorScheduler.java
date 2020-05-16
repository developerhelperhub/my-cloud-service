package com.developerhelperhub.ms.id.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

@Component
public class MonitorScheduler {

	@Autowired
	private OAuth2RestTemplate restTemplate;

	//@Scheduled(fixedDelay = 1000)
	public void monitor() {

		ResponseEntity<DiscoveryResponseModel> entity = restTemplate
				.getForEntity("http://my-cloud-discovery/discovery/applications", DiscoveryResponseModel.class);

		if (entity.getStatusCode() == HttpStatus.OK) {

			entity.getBody().getApplication().parallelStream().forEach(app -> {

				System.err.println("--------> " + app.getName());

				ResponseEntity<String> health = restTemplate
						.getForEntity("http://" + app.getName() + "/actuator/health", String.class);

				if (health.getStatusCode() == HttpStatus.OK) {

					System.err.println("= " + health.getBody());

				} else {
					System.err.print(health.getStatusCode());
				}

			});
		} else {
			System.err.print(entity.getStatusCode());
		}

	}
}
