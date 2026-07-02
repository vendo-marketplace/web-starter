# web-starter

## Overview
`web-starter` is a Spring Boot autoconfiguration starter for the Vendo ecosystem that provides a ready-to-use global exception handling layer for web-facing microservices. It auto-configures a centralized `@RestControllerAdvice`-style exception handler and validation error conversion, ensuring consistent error responses across the platform without manual setup in each service.

## Features

* **Global exception handling** — `GlobalExceptionHandler`, auto-configured via `GlobalExceptionHandlerAutoConfiguration`, catches and converts exceptions into standardized error responses
* **Configurable behavior** — `GlobalExceptionHandlerProperties` allows customizing the handler through `application.yaml` (e.g. enabling/disabling, controlling response detail level)
* **Validation error conversion** — `ValidationErrorConverter` transforms Bean Validation (`jakarta.validation`) errors into a consistent, client-friendly error format
* Auto-configuration based — activates automatically once added as a dependency, following Spring Boot starter conventions
* Built on top of `security-lib`'s `ExceptionResponse` contract for a consistent error shape across services

## Installation
Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.vendo-marketplace-be</groupId>
    <artifactId>web-starter</artifactId>
    <version>latest-version</version>
    <packaging>jar</packaging>
</dependency>
```

The starter relies on `spring-boot-starter-web` (`provided` scope), so the consuming service must already include it, along with the Vendo `core-lib` and `security-lib` dependencies.

## Usage
Once added, `web-starter` auto-configures the global exception handler via `GlobalExceptionHandlerAutoConfiguration`. No manual bean wiring is required for the default setup — any unhandled exception in a controller is automatically converted to a standardized error response.

Example — customizing behavior via `application.yaml`:

```yaml
vendo:
  web-starter:
    exception-handler:
      enabled: true
      include-stack-trace: false
```

Example — overriding the default handler with a custom bean, which takes precedence over the auto-configured default:

```java
import com.vendo.web_starter.exception.GlobalExceptionHandler;

@Bean
public GlobalExceptionHandler customGlobalExceptionHandler() {
    return new GlobalExceptionHandler(/* custom dependencies */);
}
```

## Requirements

* Java 17
* Spring Boot 3.5.x
* `spring-boot-starter-web` on the classpath
* Vendo `core-lib` and `security-lib` dependencies

## Notes
This starter is intended solely for internal use within the Vendo ecosystem (`io.github.vendo-marketplace-be`). It focuses on Spring auto-configuration for global exception handling and validation error conversion — the actual error response contract (`ExceptionResponse`) is defined in `security-lib`, keeping the response shape consistent across all services regardless of which starter produced it.