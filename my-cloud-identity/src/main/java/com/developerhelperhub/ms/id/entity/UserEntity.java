package com.developerhelperhub.ms.id.entity;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("users")
@Getter
@Setter
public class UserEntity {

	@Id
	private String username;

	private String password;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private boolean enabled;

	private Set<String> authorities;
}
