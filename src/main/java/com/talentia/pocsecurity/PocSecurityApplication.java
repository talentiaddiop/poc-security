package com.talentia.pocsecurity;

import com.talentia.pocsecurity.domain.UserEntity;
import com.talentia.pocsecurity.repository.UserEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

import static com.talentia.pocsecurity.enumeration.Role.ROLE_SUPER_ADMIN;

@SpringBootApplication
@Slf4j
@EnableFeignClients
public class PocSecurityApplication implements CommandLineRunner {
	private final UserEntityRepository repo;


	public PocSecurityApplication(UserEntityRepository repo) {
		this.repo = repo;

	}

	public static void main(String[] args) {
		SpringApplication.run(PocSecurityApplication.class, args);

	}
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(String... args) throws Exception {

		String password = "admin";
		String encodedPassword = new BCryptPasswordEncoder().encode(password);
		UserEntity superAdminUserEntity = UserEntity.builder()
				.firstName("djibi")
				.lastName("diop")
				.username("admin")
				.email("ddiopdjibi@gmail.com")
				.password(encodedPassword)
				.isActive(true)
				.isNotLocked(true)
				.authorities(ROLE_SUPER_ADMIN.getAuthorities())
				.role(ROLE_SUPER_ADMIN)
				.build();
		UserEntity save = repo.save(superAdminUserEntity);
		log.info("save super admin {}",save);

	}

}
