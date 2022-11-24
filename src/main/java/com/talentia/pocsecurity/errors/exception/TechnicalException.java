package com.talentia.pocsecurity.errors.exception;



import org.springframework.http.HttpStatus;

public class TechnicalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;


    private final HttpStatus httpStatus;

    public TechnicalException(String message) {
        super();
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public TechnicalException(HttpStatus httpStatus, String message) {
        super();
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

