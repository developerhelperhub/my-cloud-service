package com.developerhelperhub.ms.trans.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class OauthClientModel {

	private String clientId;
	private String resourceIds;
	private boolean secretRequired;
	private boolean scoped;
	private String scope;
	private String authorizedGrantTypes;
	private String registeredRedirectUri;
	private Integer accessTokenValiditySeconds;
	private Integer refreshTokenValiditySeconds;
	private boolean autoApprove;
	private Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
	private Map<String, Object> additionalInformation = new HashMap<String, Object>();

}
