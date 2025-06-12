package com.ambrosiaandrade.pets.exceptions;

import lombok.Data;

@Data
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final int statusCode;

    /**
     * Constructs a BaseException with the specified message and status code.
     *
     * @param message the error message
     * @param statusCode the HTTP status code associated with the error
     */
    public BaseException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
    
}
