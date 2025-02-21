package com.tds.desafio.service;

import com.tds.desafio.dto.UrlResponseDto;
import com.tds.desafio.mapper.UrlMapper;
import com.tds.desafio.mapper.UrlResponseMapper;
import com.tds.desafio.model.UrlModel;
import com.tds.desafio.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;

    }

    public String shortUrl(String originalUrl) {
        String shortUrl = generateShortUrl(originalUrl);
        UrlModel url = UrlMapper.toUrlModel(originalUrl, shortUrl);
        return validateUrl(url);
    }

    private String generateShortUrl(String originalUrl) {
        String cleanUrl = originalUrl.replaceAll("^(https?://)?(www\\.)?", "");
        if (cleanUrl.length() < 3) {
            throw new IllegalArgumentException("URL is too short to generate a short URL");
        }
        String shortUrl = cleanUrl.substring(0, 3);
        shortUrl = shortUrl.toLowerCase();
        shortUrl += ".com";
        return shortUrl;
    }

    private String validateUrl(UrlModel url) {
        Optional<UrlModel> existingUrl = Optional.ofNullable(repository.findByUrl(url.getUrl()));

        if (existingUrl.isEmpty()) {
            if (repository.findByShortUrl(url.getShortUrl()) == null) {
                repository.save(url);
                return url.getShortUrl();
            }
            int counter = 1;
            String baseShortUrl = url.getShortUrl().replace(".com", "");
            while (repository.findByShortUrl(url.getShortUrl()) != null) {
                url.setShortUrl(baseShortUrl + counter + ".com");
                counter++;
            }
            repository.save(url);
            return url.getShortUrl();
        }
        throw new IllegalArgumentException("Url already registered");
    }

    public String getOriginalUrl(String shortUrl) {
        Optional<UrlModel> urlModel = Optional.ofNullable(repository.findByShortUrl(shortUrl));

        if (urlModel.isPresent()) {
            UrlModel url = UrlMapper.toUrlModel(urlModel.get());
            repository.save(url);
            return url.getUrl();
        }
        throw new IllegalArgumentException();
    }

    public List<UrlResponseDto> getUrlsViewsStatistics(List<String> shortUrls) {
        List<UrlResponseDto> responseList = new ArrayList<>();

        for (String shortUrl : shortUrls) {
            UrlModel url = repository.findByShortUrl(shortUrl);

            if (url != null) {
                long daysBetween = ChronoUnit.DAYS.between(url.getCreateDate(), LocalDateTime.now());
                double averageViewsPerDay = (daysBetween > 0) ? (double) url.getTotalView() / daysBetween + 1 : url.getTotalView();
                UrlResponseDto response = UrlResponseMapper.toResponse(url, averageViewsPerDay);
                responseList.add(response);
            } else {
                responseList.add(new UrlResponseDto("Url not found:" + shortUrl, 0.0, 0));
            }
        }
        return responseList;
    }
}
