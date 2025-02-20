package com.tds.desafio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
public class UrlEditorController {
    @Autowired
    UrlService service;

    @PostMapping()
    public ResponseEntity<String> postUrl(@RequestBody String input) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.service.shortUrl(input));
    }

    @GetMapping("/{input}")
    public ResponseEntity<String> getUrl(@PathVariable String input) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.service.shortedUrl(input));
    }
}
