package com.tds.desafio.service;

import com.tds.desafio.model.UrlModel;
import com.tds.desafio.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private UrlService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shortUrl_MustGenerateANDValidateUrlCorrectly() {
        String originalUrl = "https://example.com";
        String expectedShortUrl = "exa.com";

        UrlModel mockUrlModel = new UrlModel();
        mockUrlModel.setUrl(originalUrl);
        mockUrlModel.setShortUrl(expectedShortUrl);

        when(repository.findByUrl(originalUrl)).thenReturn(null);
        when(repository.findByShortUrl(expectedShortUrl)).thenReturn(null);
        when(repository.save(any(UrlModel.class))).thenReturn(mockUrlModel);

        String actualShortUrl = service.shortUrl(originalUrl);
        assertEquals(expectedShortUrl, actualShortUrl);
    }

    @Test
    void shortUrl_MustThrowExceptionWhenUrlExists() {
        String originalUrl = "https://example.com";
        UrlModel existingUrl = new UrlModel();
        existingUrl.setUrl(originalUrl);
        existingUrl.setShortUrl("exa.com");

        when(repository.findByUrl(originalUrl)).thenReturn(existingUrl);

        assertThrows(IllegalArgumentException.class, () -> service.shortUrl(originalUrl));
    }

    @Test
    void getOriginalUrl_MustReturnOriginalUrlAndIncreaseViews() {
        String shortUrl = "exa.com";
        String originalUrl = "https://example.com";

        UrlModel urlModel = new UrlModel();
        urlModel.setUrl(originalUrl);
        urlModel.setShortUrl(shortUrl);
        urlModel.setTotalView(5);
        urlModel.setCreateDate(LocalDateTime.now().minusDays(3));

        when(repository.findByShortUrl(shortUrl)).thenReturn(urlModel);

        String actualOriginalUrl = service.getOriginalUrl(shortUrl);
        assertEquals(originalUrl, actualOriginalUrl);
        assertEquals(6, urlModel.getTotalView());
        verify(repository).save(urlModel);
    }

    @Test
    void getOriginalUrl_ShouldThrowExceptionWhenUrlDoesNotExist() {
        String shortUrl = "invalid.com";

        when(repository.findByShortUrl(shortUrl)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.getOriginalUrl(shortUrl));
    }

    @Test
    void getUrlsViewsStatistics_CorrectStatistics() {
        String shortUrl = "exa.com";
        String url = "https://example.com";
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl(shortUrl);
        urlModel.setUrl("https://example.com");
        urlModel.setTotalView(10);
        urlModel.setCreateDate(LocalDateTime.now().minusDays(5));

        when(repository.findByShortUrl(shortUrl)).thenReturn(urlModel);

        var result = service.getUrlsViewsStatistics(Arrays.asList(shortUrl));

        assertEquals(1, result.size());
        assertEquals(url, result.get(0).getUrl());
        assertEquals(10, result.get(0).getTotalView());
        assertTrue(result.get(0).getDailyView() > 2);
    }

    @Test
    void getUrlsViewsStatistics_MustReturnErrorWhenUrlDoesNtExist() {
        String shortUrl = "invalid.com";

        when(repository.findByShortUrl(shortUrl)).thenReturn(null);

        var result = service.getUrlsViewsStatistics(Arrays.asList(shortUrl));

        assertEquals(1, result.size());
        assertEquals("Url not found:" + shortUrl, result.get(0).getUrl());
        assertEquals(0, result.get(0).getTotalView());
        assertEquals(0.0, result.get(0).getDailyView());
    }
}
