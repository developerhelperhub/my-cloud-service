package com.developerhelperhub.ms.config;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Provider;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyCloudDefaultClientConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyCloudDefaultClientConfiguration.class);

	private final String OAUTH_REGISTRATION_ID = "identity-service";

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

}
