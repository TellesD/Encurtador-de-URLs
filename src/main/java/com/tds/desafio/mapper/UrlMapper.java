package com.tds.desafio.mapper;

import com.tds.desafio.model.UrlModel;

import java.time.LocalDateTime;

public class UrlMapper {

    public static UrlModel toUrlModel(String originalUrl, String shortUrl) {
        return new UrlModel(
                null,
                originalUrl,
                shortUrl,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
        );
    }
}
