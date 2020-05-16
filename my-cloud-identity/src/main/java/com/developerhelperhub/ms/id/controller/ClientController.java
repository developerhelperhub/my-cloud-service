package com.developerhelperhub.ms.id.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.trans.client.OauthClientEntity;
import com.developerhelperhub.ms.id.trans.client.OauthClientModel;
import com.developerhelperhub.ms.id.trans.client.OauthClientRepository;

@RestController
@RequestMapping("/identity/clients")
public class ClientController {

	@Autowired
	private OauthClientRepository oauthClientRepository;

	@GetMapping("/")
	public List<OauthClientModel> getClients() {
		List<OauthClientModel> list = new ArrayList<OauthClientModel>();

		for (OauthClientEntity entity : oauthClientRepository.findAll()) {

			OauthClientModel model = new OauthClientModel();
			model.setAccessTokenValiditySeconds(entity.getAccessTokenValiditySeconds());
			model.setAuthorizedGrantTypes(entity.getAuthorizedGrantTypes().toString());
			model.setAutoApprove(entity.isAutoApprove());
			model.setClientId(entity.getClientId());
			model.setRefreshTokenValiditySeconds(entity.getRefreshTokenValiditySeconds());
			model.setRegisteredRedirectUri(entity.getRegisteredRedirectUri().toString());
			model.setResourceIds(entity.getResourceIds().toString());
			model.setScope(entity.getScope().toString());
			model.setScoped(entity.isScoped());
			model.setSecretRequired(entity.isSecretRequired());

			list.add(model);
		}

		return list;
	}

}
