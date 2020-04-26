package com.developerhelperhub.ms.id.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.developerhelperhub.ms.id.entity.UserEntity;
import com.developerhelperhub.ms.id.repository.UserRepository;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class User implements UserDetails, UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6072929707082314818L;

	@Getter
	@Setter
	private String username;

	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private boolean accountNonExpired;

	@Getter
	@Setter
	private boolean accountNonLocked;

	@Getter
	@Setter
	private boolean credentialsNonExpired;

	@Getter
	@Setter
	private boolean enabled;

	@Getter
	@Setter
	private Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User() {
	}

	public User(UserEntity user) {
		this.username = user.getUsername();

		this.password = user.getPassword();

		this.accountNonExpired = user.isAccountNonExpired();

		this.accountNonLocked = user.isAccountNonLocked();

		this.credentialsNonExpired = user.isCredentialsNonExpired();

		this.enabled = user.isEnabled();

		for (String authority : user.getAuthorities()) {
			addGrantedAuthority(authority);
		}
	}

	public void create() {
		UserEntity entity = new UserEntity();

		entity.setUsername(this.username);

		entity.setPassword(passwordEncoder.encode(this.password));

		entity.setAccountNonExpired(this.accountNonExpired);

		entity.setAccountNonLocked(this.accountNonLocked);

		entity.setCredentialsNonExpired(this.credentialsNonExpired);

		entity.setEnabled(this.enabled);

		Set<String> authorities = new HashSet<String>();

		for (GrantedAuthority authority : this.authorities) {
			authorities.add("ROLE_" + authority.getAuthority());
		}
		entity.setAuthorities(authorities);

		userRepository.save(entity);

		LOGGER.debug("{} user created!", this.username);
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserEntity> entity = userRepository.findById(username);

		if (!entity.isPresent()) {
			throw new UsernameNotFoundException("Username does not found");
		}

		return new User(entity.get());
	}

	public void addGrantedAuthority(String authority) {
		this.authorities.add(new SimpleGrantedAuthority(authority));
	}
}
