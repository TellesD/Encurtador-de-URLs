package com.tds.desafio.repository;

import com.tds.desafio.model.UrlModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlRepository extends MongoRepository<UrlModel, String> {

    UrlModel findByUrl(String url);
    UrlModel findByShortUrl(String url);

}
