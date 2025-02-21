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

    public static UrlModel toUrlModel(UrlModel model) {
        UrlModel urlModel = new UrlModel ();
        urlModel.setId(model.getId());
        urlModel.setUrl(model.getUrl());
        urlModel.setShortUrl(model.getShortUrl());
        urlModel.setUpdateDate(LocalDateTime.now());
        urlModel.setCreateDate(model.getCreateDate());
        urlModel.setTotalView(model.getTotalView()+1);
        return urlModel;
    }
}
