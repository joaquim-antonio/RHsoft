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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/configuracoes")
@Tag(name = "Configurações", description = "Configurações gerais do sistema (faixas de INSS/IRRF, limites, etc.)")
@SecurityRequirement(name = "bearerAuth")
public class ConfiguracaoController {

    @Autowired private ConfiguracaoService service;

    @Operation(
        summary = "Obter configuração atual",
        description = "Retorna a configuração vigente do sistema. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuração retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @GetMapping
    public ResponseEntity<ConfiguracaoSistema> buscar() {
        ConfiguracaoSistema config = service.buscarConfiguracaoAtual();
        return ResponseEntity.ok(config);
    }

    @Operation(
        summary = "Atualizar configuração",
        description = "Atualiza a configuração do sistema. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuração atualizada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @PutMapping
    public ResponseEntity<ConfiguracaoSistema> atualizar(@RequestBody @Valid ConfiguracaoSistema config) {
        ConfiguracaoSistema atualizada = service.atualizarConfiguracao(config);
        return ResponseEntity.ok(atualizada);
    }
}