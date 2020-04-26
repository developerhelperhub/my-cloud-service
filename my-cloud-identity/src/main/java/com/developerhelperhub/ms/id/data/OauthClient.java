package com.developerhelperhub.ms.id.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Component;

import com.developerhelperhub.ms.id.entity.OauthClientEntity;
import com.developerhelperhub.ms.id.repository.OauthClientRepository;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OauthClient implements ClientDetails, ClientDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthClient.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7265763015228159319L;

	@Id
	@Getter
	@Setter
	private String clientId;

	@Getter
	@Setter
	private Set<String> resourceIds;

	@Getter
	@Setter
	private boolean secretRequired;

	@Getter
	@Setter
	private String clientSecret;

	@Getter
	@Setter
	private boolean scoped;

	@Getter
	@Setter
	private Set<String> scope;

	@Getter
	@Setter
	private Set<String> authorizedGrantTypes;

	@Getter
	@Setter
	private Set<String> registeredRedirectUri;

	@Getter
	@Setter
	private Integer accessTokenValiditySeconds;

	@Getter
	@Setter
	private Integer refreshTokenValiditySeconds;

	@Getter
	@Setter
	private boolean autoApprove;

	@Getter
	@Setter
	private Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

	@Getter
	@Setter
	private Map<String, Object> additionalInformation = new HashMap<String, Object>();

	@Autowired
	private OauthClientRepository clientRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public OauthClient() {
	}

	public OauthClient(OauthClientEntity entity) {

		this.clientId = entity.getClientId();

		this.resourceIds = entity.getResourceIds();

		this.secretRequired = entity.isSecretRequired();

		this.clientSecret = entity.getClientSecret();

		this.scoped = entity.isScoped();

		this.scope = entity.getScope();

		this.authorizedGrantTypes = entity.getAuthorizedGrantTypes();

		this.registeredRedirectUri = entity.getRegisteredRedirectUri();

		this.accessTokenValiditySeconds = entity.getAccessTokenValiditySeconds();

		this.refreshTokenValiditySeconds = entity.getRefreshTokenValiditySeconds();

		for (String authority : entity.getAuthorities()) {
			addGrantedAuthority(authority);
		}

		this.autoApprove = entity.isAutoApprove();

	}

	public void create() {

		OauthClientEntity entity = new OauthClientEntity();

		entity.setClientId(this.clientId);

		entity.setResourceIds(this.resourceIds);

		entity.setSecretRequired(this.secretRequired);

		entity.setClientSecret(passwordEncoder.encode(this.clientSecret));

		entity.setScoped(this.scoped);

		entity.setScope(this.scope);

		entity.setAuthorizedGrantTypes(this.authorizedGrantTypes);

		entity.setRegisteredRedirectUri(this.registeredRedirectUri);

		entity.setAccessTokenValiditySeconds(this.accessTokenValiditySeconds);

		entity.setRefreshTokenValiditySeconds(this.refreshTokenValiditySeconds);

		entity.setAutoApprove(this.autoApprove);

		Set<String> authorities = new HashSet<String>();

		for (GrantedAuthority authority : this.authorities) {
			authorities.add("ROLE_" + authority.getAuthority());
		}
		entity.setAuthorities(authorities);

		clientRepository.save(entity);

		LOGGER.debug("{} client created!", this.clientId);
	}

	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

		Optional<OauthClientEntity> entity = clientRepository.findById(clientId);

		if (!entity.isPresent()) {
			throw new ClientRegistrationException("Client does not found!");
		}

		return new OauthClient(entity.get());
	}

	public boolean isAutoApprove(String autoApprove) {
		this.autoApprove = Boolean.parseBoolean(autoApprove);
		return this.autoApprove;
	}

	public void addGrantedAuthority(String authority) {
		this.authorities.add(new SimpleGrantedAuthority(authority));
	}
}
