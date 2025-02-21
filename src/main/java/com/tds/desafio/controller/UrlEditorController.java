package com.tds.desafio.controller;

import com.tds.desafio.dto.StatisticRequestDto;
import com.tds.desafio.dto.UrlRequestDto;
import com.tds.desafio.dto.UrlResponseDto;
import com.tds.desafio.service.UrlService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/url")
@Tag(name = "URL Shortener", description = "Endpoints para encurtar e gerenciar URLs")
public class UrlEditorController {
    @Autowired
    UrlService service;

    @Operation(summary = "Encurtar uma URL", description = "Recebe uma URL e retorna uma versão encurtada")
    @ApiResponse(responseCode = "201", description = "URL encurtada gerada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao encurtar a URL")
    @PostMapping()
    public ResponseEntity<String> postUrl(@RequestBody UrlRequestDto input) {

        try {
            String shortUrl = this.service.shortUrl(input.getUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(shortUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }

    }

    @Hidden
    @Operation(summary = "Redirecionar para URL original", description = "Redireciona para a URL original com base no código encurtado")
    @ApiResponse(responseCode = "302", description = "Redirecionamento bem-sucedido")
    @ApiResponse(responseCode = "404", description = "URL encurtada não encontrada")
    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl) {

        try {
            String originalUrl = service.getOriginalUrl(shortUrl);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Obter estatísticas de visualização", description = "Retorna as estatísticas de visualizações das URLs encurtadas")
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro ao buscar estatísticas")
    @PostMapping("/statistic")
    public ResponseEntity<List<UrlResponseDto>> getUrlsViewsStatistics(@RequestBody StatisticRequestDto input) {
        try {
        List<UrlResponseDto> response = this.service.getUrlsViewsStatistics(input.getUrl());
        return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
}
