package io.github.vivek.linkforge.api;

import io.github.vivek.linkforge.dto.ShortenRequest;
import io.github.vivek.linkforge.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UrlController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "io.github.vivek.linkforge.*Filter"
        ))
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // enable constructor dependency injection
@Slf4j
@RequiredArgsConstructor
class UrlControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    @Test
    void shouldAcceptValidUrl() throws Exception {
        //prepare
        var shortenRequest = new ShortenRequest("https://example.com/very/long/path");
        var body = objectMapper.writeValueAsString(shortenRequest);
        var expectedValue = "my-short-url-response";
        when(urlService.generatedShortenCode(any())).thenReturn(expectedValue);

        // perform
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortenCode").exists())
                .andExpect(jsonPath("$.shortenCode").value(expectedValue));
    }

    @Test
    void shouldReturnValidationErrorForInvalidLongUrl() throws Exception {
        //prepare
        var shortenRequest = new ShortenRequest("ftp://example.com");
        var body = objectMapper.writeValueAsString(shortenRequest);

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                // status
                .andExpect(status().isBadRequest())

                // top-level fields
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))

                // error array
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(1))

                // error details
                .andExpect(jsonPath("$.errors[0].field").value("longUrl"))
                .andExpect(jsonPath("$.errors[0].error")
                        .value("longUrl must be a valid http or https URL"));
    }

    @Test
    void shouldRejectBlankUrl() throws Exception {
        //prepare
        var shortenRequest = new ShortenRequest("");
        var body = objectMapper.writeValueAsString(shortenRequest);

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}