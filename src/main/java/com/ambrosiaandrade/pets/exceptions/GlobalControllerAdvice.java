package com.ambrosiaandrade.pets.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Handles exceptions that extend BaseException, which is a custom exception class.
     *
     * @param e the BaseException thrown
     * @return a ResponseEntity containing an ErrorMessage with the status code from the exception
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(BaseException e) {
        var errorMessage = new ErrorMessage(e.getMessage(), e.getStatusCode());
        
        return ResponseEntity.status(e.getStatusCode())
                             .body(errorMessage);
    }
    
    /**
     * Handles exceptions thrown when a required request parameter is missing in the HTTP request.
     *
     * @param e the exception thrown when a required parameter is missing
     * @return a ResponseEntity containing an ErrorMessage with status 400 (Bad Request)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameterException (MissingServletRequestParameterException e) {
        var err = new ErrorMessage(e.getMessage(), 400);

        return ResponseEntity.status(err.getStatusCode())
                .body(err);
    }

    /**
     * Handles exceptions thrown when a method argument type does not match the expected type.
     *
     * @param e the exception thrown when a method argument type mismatch occurs
     * @return a ResponseEntity containing an ErrorMessage with status 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException (MethodArgumentNotValidException e) {
        var err = new ErrorMessage(e.getMessage(), 400);

        return ResponseEntity.status(err.getStatusCode())
                .body(err);
    }

    /**
     * Handles exceptions thrown when the request body is not readable, typically due to malformed JSON.
     *
     * @param e the exception thrown when the request body is not readable
     * @return a ResponseEntity containing an ErrorMessage with status 400 (Bad Request)
     */ 
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException (HttpMessageNotReadableException e) {
        var err = new ErrorMessage(e.getMessage(), 400);

        return ResponseEntity.status(err.getStatusCode())
                .body(err);
    }

    /**
     * Handles exceptions thrown when a method argument type does not match the expected type.
     *
     * @param e the exception thrown when a method argument type mismatch occurs
     * @return a ResponseEntity containing an ErrorMessage with status 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException (MethodArgumentTypeMismatchException e) {
        var err = new ErrorMessage(e.getMessage(), 400);

        return ResponseEntity.status(err.getStatusCode())
                .body(err);
    }

    /**
     * Handles exceptions thrown when there is a data integrity violation, such as a constraint violation in the database.
     *
     * @param e the exception thrown when a data integrity violation occurs
     * @return a ResponseEntity containing an ErrorMessage with status 400 (Bad Request)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException (DataIntegrityViolationException e) {
        var err = new ErrorMessage(e.getMessage(), 400);

        return ResponseEntity.status(err.getStatusCode())
                .body(err);
    }

    /**
     * Handles exceptions thrown when a requested resource is not found.
     *
     * @param e the exception thrown when a resource is not found
     * @return a ResponseEntity containing an ErrorMessage with status 404 (Not Found)
     */
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<Object> handleNotFound (Exception e) {
        var err = new ErrorMessage(e.getMessage(), 404);

        return ResponseEntity.status(err.getStatusCode())
                .body(err);
    }


    /**
     * Handles all other exceptions that are not specifically handled by the above methods.
     *
     * @param e the exception thrown
     * @return a ResponseEntity containing an ErrorMessage with status 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        var err = new ErrorMessage(e.getMessage(), 500);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(err);
    }

}