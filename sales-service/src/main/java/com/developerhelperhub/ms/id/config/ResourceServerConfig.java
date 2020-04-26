package com.developerhelperhub.ms.id.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableOAuth2Client
@EnableResourceServer
@Order(2)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServerConfig.class);

	private static final String RESOURCE_ID = "sales_service_resource_id";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID).stateless(false).tokenServices(tokenServices());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable().authorizeRequests().antMatchers("/**").access("hasRole('USER')").and()
				.exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}

	@Bean
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(tokenStore());
		return tokenServices;
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("123456");
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	@LoadBalanced
	public OAuth2RestTemplate identityRestTemplate() {
		final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setId("identity-service");
		resourceDetails.setClientId("my-cloud-identity-credentials");
		resourceDetails.setClientSecret("VkZpzzKa3uMq4vqg");
		resourceDetails.setGrantType("client_credentials");
		resourceDetails.setScope(Arrays.asList("ADMIN"));
		resourceDetails.setAccessTokenUri("http://localhost:8081/auth/oauth/token");

		OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails);

		return template;
	}
}
