package com.talentia.pocsecurity.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
		info =@Info(
				title = "User API",
				//version = "${api.version:}",
				contact = @Contact(
						name = "djibi diop", email = "ddiopdjibi@gmail.com", url = "https://talentia.com"
				),
				license = @License(
						name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
				),
				termsOfService = "${tos.uri}",
				description = "${api.description}"
		)

)

public class OpenAPISecurityConfiguration {}