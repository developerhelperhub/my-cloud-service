package com.developerhelperhub.ms.id.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developerhelperhub.ms.id.trans.user.UserEntity;
import com.developerhelperhub.ms.id.trans.user.UserModel;
import com.developerhelperhub.ms.id.trans.user.UserRepository;

@RestController
@RequestMapping("/identity/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/me")
	public Principal user(Principal principal) {
		return principal;
	}

	@GetMapping("/")
	public List<UserModel> getUsers() {
		List<UserModel> list = new ArrayList<UserModel>();

		for (UserEntity entity : userRepository.findAll()) {

			UserModel model = new UserModel();

			model.setAccountNonExpired(entity.isAccountNonExpired());
			model.setAccountNonLocked(entity.isAccountNonLocked());

			model.setAuthorities(entity.getAuthorities().toString());

			model.setCredentialsNonExpired(entity.isCredentialsNonExpired());
			model.setEnabled(entity.isEnabled());
			model.setUsername(entity.getUsername());
			
			list.add(model);
		}

		return list;
	}
}
