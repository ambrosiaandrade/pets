package com.ambrosiaandrade.pets.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorMessage {

    private String message;
    private int statusCode;
    private LocalDateTime timestamp;

    /**
     * Constructs an ErrorMessage with the specified message and status code.
     *
     * @param message the error message
     * @param statusCode the HTTP status code associated with the error
     */
    public ErrorMessage(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

}
