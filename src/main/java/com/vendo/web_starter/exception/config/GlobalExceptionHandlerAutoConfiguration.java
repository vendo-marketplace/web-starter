package com.vendo.web_starter.exception.config;

import com.vendo.web_starter.exception.GlobalExceptionHandler;
import com.vendo.web_starter.exception.props.GlobalExceptionHandlerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(GlobalExceptionHandlerProperties.class)
public class GlobalExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "vendo.web.global-exception-handler",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
