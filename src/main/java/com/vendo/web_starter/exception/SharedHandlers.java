package com.vendo.web_starter.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vendo.core_lib.constants.Delimiters;
import com.vendo.core_lib.utils.ClassFields;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.security_lib.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

final class SharedHandlers {

    static ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException e, String path) {
        String fieldName = getJacksonFieldName(e.getPath());
        String[] enumValues = ClassFields.getEnumValues(e.getTargetType());

        if (StringUtils.isEmpty(fieldName) || enumValues.length == 0) {
            return ResponseEntity.internalServerError().body(HandlerUtils.buildInternalErrorBody(path));
        }

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(Map.of(fieldName, "Allowed types are: " + String.join(Delimiters.COMMA_DELIMITER, enumValues)))
                .code(HttpStatus.BAD_REQUEST.value())
                .path(path)
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    private static String getJacksonFieldName(List<JsonMappingException.Reference> references) {
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
