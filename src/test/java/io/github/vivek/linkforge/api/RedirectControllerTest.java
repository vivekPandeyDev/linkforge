package io.github.vivek.linkforge.api;

import io.github.vivek.linkforge.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = RedirectController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "io.github.vivek.linkforge.*Filter"
        )
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // enable constructor dependency injection
@Slf4j
@RequiredArgsConstructor
class RedirectControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void shouldRedirectToResolvedUrl() throws Exception {
        // given
        String code = "abc123";
        when(urlService.resolvedUrl(code)).thenReturn("https://example.com/very/long/path");

        // when + then
        mockMvc.perform(get("/api/v1/" + code))
                .andExpect(status().isFound()) // 302
                .andExpect(header().string(
                        "Location",
                        "https://example.com/very/long/path"
                ));
    }

    @Test
    void shouldReturnValidationErrorForBlankCode() throws Exception {
        // whitespace path variable → triggers @NotBlank
        mockMvc.perform(get("/api/v1/ "))
                .andExpect(status().isBadRequest())

                // top-level fields
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed"))

                // errors array
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(1))

                // error details
                .andExpect(jsonPath("$.errors[0].field").value("code"))
                .andExpect(jsonPath("$.errors[0].error")
                        .value("url shorten code should not be empty"));
    }

}