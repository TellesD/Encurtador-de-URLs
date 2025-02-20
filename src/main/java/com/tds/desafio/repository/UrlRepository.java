package com.tds.desafio.repository;

import com.tds.desafio.model.Url;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlRepository extends MongoRepository<Url, String> {

    Url findByUrl(String url);

    Url FindByShortUrl(String url);
}
