package com.talentia.pocsecurity.config;



import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {
	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
	//		requestTemplate.header("Authorization", username);
//			requestTemplate.header("password", password);
//			requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
		};
	}
}