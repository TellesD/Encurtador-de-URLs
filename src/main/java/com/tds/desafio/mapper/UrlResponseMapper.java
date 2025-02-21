package com.tds.desafio.mapper;

import com.tds.desafio.dto.UrlResponseDto;
import com.tds.desafio.model.UrlModel;

public class UrlResponseMapper {
    public static UrlResponseDto toResponse(UrlModel urlModel, double dailyView) {
        UrlResponseDto urlResponseDto = new UrlResponseDto();
        urlResponseDto.setUrl(urlModel.getUrl());
        urlResponseDto.setTotalView(urlModel.getTotalView());
        urlResponseDto.setDailyView(dailyView);
        return urlResponseDto;
    }
}
