package com.developerhelperhub.ms.config;

import com.developerhelperhub.ms.config.AbstractMyCloundAuthenticationEntryPoint;

public class AuthenticationEntryPointImpl extends AbstractMyCloundAuthenticationEntryPoint {

	@Override
	protected String getServiceRealName() {
		return "my-cloud-api-gateway";
	}
}
