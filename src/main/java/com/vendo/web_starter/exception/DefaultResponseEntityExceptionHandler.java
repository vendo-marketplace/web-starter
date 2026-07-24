package com.vendo.web_starter.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.security_lib.exception.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class DefaultResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(DefaultResponseEntityExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        Map<String, String> errors = ValidationErrorConverter.fromField(ex.getBindingResult().getFieldErrors());

        ExceptionResponse response = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(errors)
                .code(HttpStatus.BAD_REQUEST.value())
                .path(path)
                .build();

        return ResponseEntity.status(status.value()).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("File size upload exceeded.")
                .code(HttpStatus.BAD_REQUEST.value())
                .path(path)
                .build();
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        Map<String, String> errors = ValidationErrorConverter.fromParameter(ex.getParameterValidationResults());

        ExceptionResponse response = ExceptionResponse.builder()
                .message("Validation failed.")
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .path(path)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage());
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        if (ex.getCause() instanceof InvalidFormatException cause) {
            return SharedHandlers.handleInvalidFormatException(cause, path);
        }

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Invalid body structure.")
                .code(HttpStatus.BAD_REQUEST.value())
                .path(path)
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(Map.of(ex.getParameterName(), getDefaultMissingValueMessage(ex.getParameterName(), ex.getBody())))
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(Map.of(ex.getRequestPartName(), getDefaultMissingValueMessage(ex.getRequestPartName(), ex.getBody())))
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

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

    private String getDefaultMissingValueMessage(String fieldName, ProblemDetail problemDetail) {
        if (problemDetail != null && !StringUtils.isEmpty(problemDetail.getDetail())) {
            return problemDetail.getDetail();
        }

        return "%s parameter is missing.".formatted(fieldName);
    }

}
