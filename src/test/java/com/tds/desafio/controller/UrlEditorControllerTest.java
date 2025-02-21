package com.tds.desafio.controller;

import com.tds.desafio.dto.UrlResponseDto;
import com.tds.desafio.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UrlEditorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlEditorController urlEditorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlEditorController).build();
    }

    @Test
    public void testPostUrl_ShouldReturnCreatedResponse() throws Exception {
        String originalUrl = "http://example.com";
        String shortUrl = "http://short.url/exa.com";

        when(urlService.shortUrl(originalUrl)).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"" + originalUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortUrl));
    }

    @Test
    public void testRedirectToOriginalUrl_ShouldReturnFoundResponse() throws Exception {
        String shortUrl = "exa.com";
        String originalUrl = "http://example.com";

        when(urlService.getOriginalUrl(shortUrl)).thenReturn(originalUrl);

        mockMvc.perform(get("/url/{shortUrl}", shortUrl))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    public void testRedirectToOriginalUrl_ShouldReturnNotFoundResponse() throws Exception {
        String shortUrl = "exa.com";

        when(urlService.getOriginalUrl(shortUrl)).thenThrow(new IllegalArgumentException("URL not found"));

        mockMvc.perform(get("/url/{shortUrl}", shortUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUrlsViewsStatistics_ShouldReturnOkResponse() throws Exception {
        List<String> shortUrls = Arrays.asList("abc123", "xyz456");
        List<UrlResponseDto> urlResponseDtoList = Arrays.asList(
                new UrlResponseDto("abc123", 5.0, 10),
                new UrlResponseDto("xyz456", 3.0, 6)
        );

        when(urlService.getUrlsViewsStatistics(shortUrls)).thenReturn(urlResponseDtoList);

        mockMvc.perform(post("/url/statistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": [\"abc123\", \"xyz456\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetUrlsViewsStatistics_ShouldReturnBadRequestResponse() throws Exception {
        List<String> shortUrls = Arrays.asList("abc123", "xyz456");

        when(urlService.getUrlsViewsStatistics(shortUrls)).thenThrow(new IllegalArgumentException("Invalid URL"));

        mockMvc.perform(post("/url/statistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": [\"abc123\", \"xyz456\"]}"))
                .andExpect(status().isBadRequest());
    }
}