package com.tds.desafio.service;

import com.tds.desafio.model.UrlModel;
import com.tds.desafio.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
        when(repository.save(any(UrlModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String actualOriginalUrl = service.getOriginalUrl(shortUrl);
        assertEquals(originalUrl, actualOriginalUrl);
        ArgumentCaptor<UrlModel> captor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).save(captor.capture());
        UrlModel savedUrl = captor.getValue();
        assertEquals(6, savedUrl.getTotalView());
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

        var result = service.getUrlsViewsStatistics(List.of(shortUrl));

        assertEquals(1, result.size());
        assertEquals(url, result.getFirst().getUrl());
        assertEquals(10, result.getFirst().getTotalView());
        assertTrue(result.getFirst().getDailyView() > 2);
    }

    @Test
    void getUrlsViewsStatistics_MustReturnErrorWhenUrlDoesNtExist() {
        String shortUrl = "invalid.com";

        when(repository.findByShortUrl(shortUrl)).thenReturn(null);

        var result = service.getUrlsViewsStatistics(List.of(shortUrl));

        assertEquals(1, result.size());
        assertEquals("Url not found:" + shortUrl, result.getFirst().getUrl());
        assertEquals(0, result.getFirst().getTotalView());
        assertEquals(0.0, result.getFirst().getDailyView());
    }
}
