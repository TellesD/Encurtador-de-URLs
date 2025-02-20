package com.tds.desafio.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Url {
    @Id
    private String id;
    private String url;
    private String shortUrl;
    private Date date;
    private int dailyView;
    private int totalView;

}
