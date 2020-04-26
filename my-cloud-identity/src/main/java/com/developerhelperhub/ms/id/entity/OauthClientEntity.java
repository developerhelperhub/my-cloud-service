package com.developerhelperhub.ms.id.entity;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("oauth_clients")
@Getter
@Setter
public class OauthClientEntity {

	@Id
	private String clientId;

	private Set<String> resourceIds;

	private boolean secretRequired;

	private String clientSecret;

	private boolean scoped;

	private Set<String> scope;

	private Set<String> authorizedGrantTypes;

	private Set<String> registeredRedirectUri;

	private Set<String> authorities;

	private Integer accessTokenValiditySeconds;

	private Integer refreshTokenValiditySeconds;

	private boolean autoApprove;

}
