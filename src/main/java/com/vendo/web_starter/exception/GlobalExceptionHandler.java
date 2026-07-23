package com.vendo.web_starter.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class})
    protected ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Handling internal exception: {}.", e.getMessage());

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Internal server error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @ExceptionHandler(InvalidFormatException.class)
    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException e, String path) {
        return SharedHandlers.handleInvalidFormatException(e, path);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Map<String, String> errors = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(this::lastPropertyNode, ConstraintViolation::getMessage, (first, second) -> first));

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(errors)
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    private String lastPropertyNode(ConstraintViolation<?> violation) {
        String name = "";
        for (Path.Node node : violation.getPropertyPath()) {
            name = node.getName();
        }
        return name;
    }

}
