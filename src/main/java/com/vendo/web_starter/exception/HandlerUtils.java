package com.vendo.web_starter.exception;

import com.vendo.security_lib.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;

final class HandlerUtils {

    static ExceptionResponse buildInternalErrorBody(String path) {
        return ExceptionResponse.builder()
                .message("Internal server error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(path)
                .build();
    }

}
