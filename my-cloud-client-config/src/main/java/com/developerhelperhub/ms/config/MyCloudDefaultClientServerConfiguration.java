package com.developerhelperhub.ms.config;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Provider;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyCloudDefaultClientServerConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyCloudDefaultClientServerConfiguration.class);

	private final String OAUTH_REGISTRATION_ID = "identity-service";

	@Autowired
	private Environment environment;

	@Bean
	public OAuth2ClientProperties oAuth2ClientProperties() {
		return new OAuth2ClientProperties();
	}

	@Bean
	@LoadBalanced
	public RestTemplate identityRestTemplate() {
		OAuth2ClientProperties oauth2Properties = oAuth2ClientProperties();

		RestTemplate template;

		if (oauth2Properties.getRegistration().containsKey(OAUTH_REGISTRATION_ID)
				&& oauth2Properties.getProvider().containsKey(OAUTH_REGISTRATION_ID)) {

			Registration reg = oauth2Properties.getRegistration().get(OAUTH_REGISTRATION_ID);

			final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
			resourceDetails.setId(OAUTH_REGISTRATION_ID);
			resourceDetails.setClientId(reg.getClientId());
			resourceDetails.setClientSecret(reg.getClientSecret());
			resourceDetails.setGrantType(reg.getAuthorizationGrantType());
			resourceDetails.setScope(new ArrayList<>(reg.getScope()));

			Provider provider = oauth2Properties.getProvider().get(reg.getProvider());

			resourceDetails.setAccessTokenUri(provider.getTokenUri());

			template = new OAuth2RestTemplate(resourceDetails);

		} else {

			LOGGER.warn("Oauth2 configuration not found!");

			template = new RestTemplate();
		}

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
