package com.tds.desafio.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class StatisticRequestDto {
    private List<String> url;
}
