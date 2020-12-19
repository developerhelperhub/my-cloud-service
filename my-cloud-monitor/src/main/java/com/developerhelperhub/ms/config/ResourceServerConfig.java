package com.developerhelperhub.ms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.developerhelperhub.ms.config.AbstractMyCloudDefaultResourceServerConfig;

@Configuration
@Order(1)
@EnableResourceServer
public class ResourceServerConfig extends AbstractMyCloudDefaultResourceServerConfig {

	@Value("${mycloud.identity.jwt.sign-key}")
	private String jwtSignKey;

	@Override
	protected String getResourceId() {
		return "my_cloud_monitor_id";
	}

	@Override
	public String getIdentityJwtSignKey() {
		return jwtSignKey;
	}

}
