package com.vendo.web_starter.exception.config;

import com.vendo.web_starter.exception.GlobalExceptionHandler;
import com.vendo.web_starter.exception.ValidationExceptionHandler;
import com.vendo.web_starter.exception.props.ValidationExceptionHandlerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(ValidationExceptionHandlerProperties.class)
public class ValidationExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "vendo.web.exception-handler",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public ValidationExceptionHandler validationExceptionHandler() {
        return new ValidationExceptionHandler();
    }

}
