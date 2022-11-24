package com.talentia.pocsecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.talentia.pocsecurity.constant.SecurityConstant.PUBLIC_URLS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class SecurityConfig  {
	private final JwtAuthorizationFilter jwtAuthorizationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jWTAccessDeniedHandler;
	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
	public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter,
                          @Qualifier("userDetailsService")UserDetailsService userDetailsService,
                          BCryptPasswordEncoder bCryptPasswordEncoder, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jWTAccessDeniedHandler) {
	    this.jwtAuthorizationFilter = jwtAuthorizationFilter;
	    this.userDetailsService = userDetailsService;
	    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
	    this.jWTAccessDeniedHandler = jWTAccessDeniedHandler;
    }
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
		return authConfiguration.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(bCryptPasswordEncoder);

		return authProvider;
	}






	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//http.authenticationProvider(authenticationProvider());
		http.csrf().disable().cors().and()
				.sessionManagement().sessionCreationPolicy(STATELESS)
				.and()
				.authorizeRequests().antMatchers(PUBLIC_URLS).permitAll()
				.anyRequest().authenticated()
				.and()
				.exceptionHandling().accessDeniedHandler(jWTAccessDeniedHandler)
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.and()
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
				;

		return http.build();
	}


}

