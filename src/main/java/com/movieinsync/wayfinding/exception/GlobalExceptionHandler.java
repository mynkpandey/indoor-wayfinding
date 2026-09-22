package com.movieinsync.wayfinding.exception;

import com.movieinsync.wayfinding.model.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRouteNotFound(
            RouteNotFoundException exception) {

        ErrorResponse response =
                new ErrorResponse(
                        "NO_ROUTE",
                        exception.getMessage()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception) {

        logger.error(
                "Unexpected application error",
                exception
        );

        ErrorResponse response =
                new ErrorResponse(
                        "ERROR",
                        "An unexpected error occurred while processing the request."
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request.");

        ErrorResponse response =
                new ErrorResponse(
                        "INVALID_REQUEST",
                        message
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}