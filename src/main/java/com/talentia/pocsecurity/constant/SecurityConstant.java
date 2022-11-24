package com.talentia.pocsecurity.constant;

public class SecurityConstant {

	public static final long EXPIRATION_TIME = 1800000; // 30 second expressed in milliseconds
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String JWT_TOKEN_HEADER = "token";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token Cannot be verified";
	public static final String GET_ARRAYS_LLC = "Get Arrays, LLC.";
	public static final String GET_ARRAYS_ADMINISTRATION = "User Demat Portal";
	public static final String AUTHORITIES = "authorities";
	public static final String FORBIDDEN_MESSAGE = "You need to log in to access this resource";
	public static final String UNAUTHORIZED_MESSAGE = "You do not have authorized to access this resource";
	public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource";
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
	public static final String[] PUBLIC_URLS = { "/api/user/login", "/api/user/register", "/api/user/resetpassword/**", "/api/user/image/**","/swagger-ui/**", "/poc-api-openapi/**" ,"/v3/api-docs"};
}

