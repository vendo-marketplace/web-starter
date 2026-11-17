package com.vendo.web_starter.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ExceptionHandlerOrderTest.TestApplication.class)
@AutoConfigureMockMvc
class ExceptionHandlerOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void enumRequestParam_shouldReturnBadRequest_whenValueIsInvalid() throws Exception {
        mockMvc.perform(get("/test/enum").param("type", "invalid_type"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.errors.type").value("Allowed values are: FIRST, SECOND."))
                .andExpect(jsonPath("$.path").value("/test/enum"));
    }

    @Test
    void illegalArgumentException_shouldReturnInternalServerError_whenThrownByController() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error."));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(TestController.class)
    static class TestApplication {
    }

    enum TestType {
        FIRST, SECOND
    }

    @RestController
    static class TestController {

        @GetMapping("/test/enum")
        TestType enumParam(@RequestParam TestType type) {
            return type;
        }

        @GetMapping("/test/illegal-argument")
        void illegalArgument() {
            throw new IllegalArgumentException("test");
        }
    }
}
