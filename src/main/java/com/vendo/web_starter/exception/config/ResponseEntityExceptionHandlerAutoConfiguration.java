package com.vendo.web_starter.exception.config;

import com.vendo.web_starter.exception.DefaultResponseEntityExceptionHandler;
import com.vendo.web_starter.exception.props.ResponseEntityExceptionHandlerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(ResponseEntityExceptionHandlerProperties.class)
public class ResponseEntityExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "vendo.web.response-entity-exception-handler",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public DefaultResponseEntityExceptionHandler responseEntityExceptionHandler() {
        return new DefaultResponseEntityExceptionHandler();
    }

}
