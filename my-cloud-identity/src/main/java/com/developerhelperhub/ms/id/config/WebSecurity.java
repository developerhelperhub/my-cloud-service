package com.developerhelperhub.ms.id.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.developerhelperhub.ms.id.data.User;

@Order(1)
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private User user;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers().antMatchers("/login", "/oauth/token", "/oauth/authorize").and().authorizeRequests()
				.anyRequest().authenticated().and().formLogin().permitAll();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(user);

		// auth.inMemoryAuthentication().withUser(username).password(passwordEncoder().encode(password)).roles("USER");
	}

	/**
	 * AuthenticationManager bean is creating. This bean is required to configured
	 * in the authorization server to support the grant type "password".
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
