package com.developerhelperhub.ms.id;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.developerhelperhub.ms.id.trans.client.OauthClient;
import com.developerhelperhub.ms.id.trans.user.User;

@SpringBootApplication
@EnableDiscoveryClient
public class IdentityServiceApplication implements CommandLineRunner {

	@Autowired
	private User user;

	@Autowired
	private OauthClient client;

	public static void main(String[] args) {
		SpringApplication.run(IdentityServiceApplication.class, args);
	}

	public void run(String... args) throws Exception {
		user.setUsername("mycloud");
		user.setPassword("mycloud@1234");
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setEnabled(true);

		// Added the user role
		user.addGrantedAuthority("ADMIN");
		user.addGrantedAuthority("USER");

		user.create();

		client.setClientId("my-cloud-identity");
		client.setClientSecret("VkZpzzKa3uMq4vqg");

		// Added the new resources id's
		client.setResourceIds(new HashSet<String>(Arrays.asList("my_cloud_identity_id", "my_cloud_api_gateway_id",
				"my_cloud_discovery_id", "my_cloud_circuit_breaker_id", "inventory_service_resource_id",
				"api_gateway_resource_id", "sales_service_resource_id")));

		client.addGrantedAuthority("ADMIN");

		client.setSecretRequired(true);
		client.setScoped(true);
		client.setScope(new HashSet<String>(Arrays.asList("user_info")));
		client.setAuthorizedGrantTypes(new HashSet<String>(
				Arrays.asList("authorization_code", "password", "refresh_token", "client_credentials")));
		client.setRegisteredRedirectUri(new HashSet<String>(Arrays.asList("http://localhost:8082/login/oauth2/code/")));
		client.setAccessTokenValiditySeconds(43199);
		client.setRefreshTokenValiditySeconds(83199);
		client.setAutoApprove(true);

		client.create();

		client.setClientId("my-cloud-identity-credentials");
		client.setClientSecret("VkZpzzKa3uMq4vqg");

		// Added the new resources id's
		client.setResourceIds(new HashSet<String>(Arrays.asList("my_cloud_identity_id", "my_cloud_api_gateway_id",
				"my_cloud_discovery_id", "my_cloud_circuit_breaker_id", "inventory_service_resource_id",
				"api_gateway_resource_id", "sales_service_resource_id")));

		client.addGrantedAuthority("ADMIN");

		client.setSecretRequired(true);
		client.setScoped(true);
		client.setScope(new HashSet<String>(Arrays.asList("ADMIN")));
		client.setAuthorizedGrantTypes(new HashSet<String>(Arrays.asList("client_credentials")));
		client.setAccessTokenValiditySeconds(43199);
		client.setRefreshTokenValiditySeconds(83199);
		client.setAutoApprove(true);

		client.create();

	}

}
