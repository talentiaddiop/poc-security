package com.talentia.pocsecurity.listener;

import com.talentia.pocsecurity.domain.UserEntity;
import com.talentia.pocsecurity.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationSuccessListener {
	private LoginAttemptService loginAttemptService;

	@Autowired
	public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof UserEntity) {
			UserEntity userEntity = (UserEntity) event.getAuthentication().getPrincipal();
			loginAttemptService.evictUserFromLoginAttemptCache(userEntity.getUsername());
		}
	}
}
