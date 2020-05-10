package com.developerhelperhub.ms.id.trans.user;

import lombok.Data;

@Data
public class UserModel {

	private String username;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private boolean enabled;

	private String authorities;

}
