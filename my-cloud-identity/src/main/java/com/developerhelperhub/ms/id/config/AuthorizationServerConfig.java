package com.developerhelperhub.ms.id.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.developerhelperhub.ms.id.data.OauthClient;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private AuthenticationManager authenticationManager;

	private UserDetailsService userDetailsService;

	private OauthClient oauthClientDetail;

	@Autowired
	public AuthorizationServerConfig(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			OauthClient oauthClientDetail) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.oauthClientDetail = oauthClientDetail;
	}

	/**
	 * Spring Security OAuth exposes two endpoints for checking tokens
	 * (/oauth/check_token and /oauth/token_key). Those endpoints are not exposed by
	 * default (have access "denyAll()").
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService)
				.accessTokenConverter(accessTokenConverter());
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

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(oauthClientDetail);

//		clients.inMemory().withClient(clientID).secret(passwordEncoder.encode(clientSecret))
//				.authorizedGrantTypes("authorization_code", "password", "refresh_token").scopes("user_info")
//				.autoApprove(true).redirectUris(redirectURLs).refreshTokenValiditySeconds(83199)
//				.accessTokenValiditySeconds(43199);

	}
}
