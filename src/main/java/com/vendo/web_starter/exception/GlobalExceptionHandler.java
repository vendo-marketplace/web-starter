package com.vendo.web_starter.exception;

import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Unsupported media type.")
                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ExceptionResponse response = ExceptionResponse.builder()
                .message("Method not allowed.")
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED.value()).body(response);
    }

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

}
