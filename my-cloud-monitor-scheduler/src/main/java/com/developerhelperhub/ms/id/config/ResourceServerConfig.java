package com.developerhelperhub.ms.id.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@Order(1)
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "my_cloud_monitor_scheduler_id";

	@Value("${mycloud.oauth.id}")
	private String identityId;

	@Value("${mycloud.oauth.client-id}")
	private String identityClientId;

	@Value("${mycloud.oauth.client-secret}")
	private String identityClientSecret;

	@Value("${mycloud.oauth.grant-type}")
	private String identityGrantType;

	@Value("${mycloud.oauth.scop}")
	private String identityScop;

	@Value("${mycloud.oauth.access-token-uri}")
	private String identityAccessTokenUri;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID).stateless(true).tokenServices(tokenServices());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable().authorizeRequests().antMatchers("/**").access("hasRole('ADMIN')").and()
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
		JwtTokenStore store = new JwtTokenStore(accessTokenConverter());
		return store;
	}

	@Bean("loadBalance")
	@LoadBalanced
	public OAuth2RestTemplate identityRestTemplateLoadBalance() {

		final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();

		resourceDetails.setId(this.identityId);
		resourceDetails.setClientId(this.identityClientId);
		resourceDetails.setClientSecret(this.identityClientSecret);
		resourceDetails.setGrantType(this.identityGrantType);
		resourceDetails.setScope(Arrays.asList(this.identityScop));
		resourceDetails.setAccessTokenUri(this.identityAccessTokenUri);

		OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails);

		return template;
	}
	
	@Bean("nonLoadBalance")
	public OAuth2RestTemplate identityRestTemplate() {

		final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();

		resourceDetails.setId(this.identityId);
		resourceDetails.setClientId(this.identityClientId);
		resourceDetails.setClientSecret(this.identityClientSecret);
		resourceDetails.setGrantType(this.identityGrantType);
		resourceDetails.setScope(Arrays.asList(this.identityScop));
		resourceDetails.setAccessTokenUri(this.identityAccessTokenUri);

		OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails);

		return template;
	}

}
