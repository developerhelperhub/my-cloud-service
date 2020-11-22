package com.developerhelperhub.ms.id.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@Configuration
public class ConfigServerConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServerConfiguration.class);

	@Autowired
	private Environment environment;

	public OAuth2RestTemplate identityRestTemplate() {
		final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setId("identity-service");
		resourceDetails.setClientId("my-cloud-identity-credentials");
		resourceDetails.setClientSecret("VkZpzzKa3uMq4vqg");
		resourceDetails.setGrantType("client_credentials");
		resourceDetails.setScope(Arrays.asList("ADMIN"));
		resourceDetails.setAccessTokenUri("http://localhost:8081/oauth/token");

		OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails);

		return template;
	}

	@Bean
	public ConfigClientProperties configClientProperties() {

		ConfigClientProperties client = new ConfigClientProperties(this.environment);
		client.setEnabled(true);

		return client;
	}

	/**
	 * In resources/META-INF, create a file called spring.factories and specify your
	 * custom configuration, as shown in the following example: spring.factories.
	 * 
	 * org.springframework.cloud.bootstrap.BootstrapConfiguration =
	 * com.developerhelperhub.ms.id.config.ConfigServerConfiguration
	 * 
	 * @return
	 */

	@Bean
	public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {

		ConfigClientProperties clientProperties = configClientProperties();

		ConfigServicePropertySourceLocator configServicePropertySourceLocator = new ConfigServicePropertySourceLocator(
				clientProperties);
		configServicePropertySourceLocator.setRestTemplate(identityRestTemplate());

		return configServicePropertySourceLocator;
	}
}
