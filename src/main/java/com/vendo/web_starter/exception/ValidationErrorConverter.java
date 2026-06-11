package com.vendo.web_starter.exception;

import com.vendo.core_lib.utils.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

class ValidationErrorConverter {

    static Map<String, String> fromField(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> StringUtils.defaultIfEmpty(fieldError.getDefaultMessage(),
                                "No error message."))
                );
    }

    static Map<String, String> fromParameter(List<ParameterValidationResult> parameterErrors) {
        Map<String, String> errors = new TreeMap<>();

        for (ParameterValidationResult error : parameterErrors) {
            String parameterName = error.getMethodParameter().getParameterName();

            String message = error.getResolvableErrors()
                    .stream()
                    .findFirst()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .orElse("No error message.");

            errors.put(parameterName, message);
        }

        return errors;
    }
}
