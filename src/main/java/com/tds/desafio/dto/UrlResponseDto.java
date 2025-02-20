package com.tds.desafio.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UrlResponseDto {

    private String url;
    private int dailyView;
    private int totalView;
}
