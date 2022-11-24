package com.talentia.pocsecurity.errors;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.talentia.pocsecurity.errors.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;


import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class ExceptionHandling {

    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    private static final String INCORRECT_FIELD = "the data structure is incorrect";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    private static final String ERROR_PATH = "/error";
    public static final String TOKEN_HAS_EXPIRED = "The Token has expired";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<HttpResponse> businessException(BusinessException ex) {
        return createHttpResponse(ex.getHttpStatus(), ex.getMessage());
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<HttpResponse> technicalException(TechnicalException ex) {
        return createHttpResponse(ex.getHttpStatus(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpResponse> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    private ResponseEntity<HttpResponse> processFieldErrors(List<FieldError> fieldErrors) {
        ResponseEntity<HttpResponse> error = createHttpResponse(BAD_REQUEST, INCORRECT_FIELD);
        fieldErrors.stream().forEach(fe ->
                error.getBody().addFieldError(fe.getObjectName(), fe.getField(), fe.getDefaultMessage())
        );
        return error;
    }


    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<HttpResponse> tokenExpiredException() {
        return createHttpResponse(UNAUTHORIZED, TOKEN_HAS_EXPIRED);
    }
    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }




    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException exception) {
        return createHttpResponse(BAD_REQUEST, "There is no page for a " + exception.getHttpMethod() + " request on path " + exception.getRequestURL());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NoResultException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }


    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }


    @RequestMapping(ERROR_PATH)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<HttpResponse> pageNotFound() {
        return createHttpResponse(NOT_FOUND, "resource not exist");
    }

    //@Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
