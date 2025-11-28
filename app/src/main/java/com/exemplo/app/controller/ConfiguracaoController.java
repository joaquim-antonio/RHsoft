package com.exemplo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.service.ConfiguracaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/configuracoes")
public class ConfiguracaoController {

    @Autowired private ConfiguracaoService service;

    @GetMapping
    public ResponseEntity<ConfiguracaoSistema> buscar() {
        ConfiguracaoSistema config = service.buscarConfiguracaoAtual();
        return ResponseEntity.ok(config);
    }

    @PutMapping
    public ResponseEntity<ConfiguracaoSistema> atualizar(@RequestBody @Valid ConfiguracaoSistema config) {
        ConfiguracaoSistema atualizada = service.atualizarConfiguracao(config);
        return ResponseEntity.ok(atualizada);
    }
}