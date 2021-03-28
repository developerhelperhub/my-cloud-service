package com.developerhelperhub.ms.config;

public class AuthenticationEntryPointImpl extends AbstractMyCloundAuthenticationEntryPoint {
	
	@Override
	protected String getServiceRealName() {
		return "discovery-service";
	}
}
