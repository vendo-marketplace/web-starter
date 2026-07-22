package com.vendo.web_starter.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vendo.core_lib.constants.Delimiters;
import com.vendo.core_lib.utils.ClassFields;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    private static ExceptionResponse buildInternalErrorBody(String path) {
        return ExceptionResponse.builder()
                .message("Internal server error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(path)
                .build();
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
            return handleInvalidFormatException(cause, path);
        }

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Invalid body structure.")
                .code(HttpStatus.BAD_REQUEST.value())
                .path(path)
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(InvalidFormatException.class)
    private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException e, String path) {
        String fieldName = getJacksonFieldName(e.getPath());
        String[] enumValues = ClassFields.getEnumValues(e.getTargetType());

        if (StringUtils.isEmpty(fieldName) || enumValues.length == 0) {
            return ResponseEntity.internalServerError().body(buildInternalErrorBody(path));
        }

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(Map.of(fieldName, "Allowed types are: " + String.join(Delimiters.COMMA_DELIMITER, enumValues)))
                .code(HttpStatus.BAD_REQUEST.value())
                .path(path)
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    private String lastPropertyNode(ConstraintViolation<?> violation) {
        String name = "";
        for (Path.Node node : violation.getPropertyPath()) {
            name = node.getName();
        }
        return name;
    }

    private String getJacksonFieldName(List<JsonMappingException.Reference> references) {
        String fieldName = "";
        int lastIndex = references.size() - 1;

        if (!references.isEmpty()) {
            String lastFieldName = references.get(lastIndex).getFieldName();
            if (!StringUtils.isEmpty(lastFieldName)) {
                fieldName = lastFieldName;
            }
        }

        return fieldName;
    }

}
