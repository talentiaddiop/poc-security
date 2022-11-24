package com.talentia.pocsecurity.errors.exception;



import org.springframework.http.HttpStatus;

public class  BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;


    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        super();
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(HttpStatus httpStatus, String message) {
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

