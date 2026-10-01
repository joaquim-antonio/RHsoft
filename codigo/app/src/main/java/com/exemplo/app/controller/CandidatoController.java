package com.exemplo.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.CandidatoProfileDTO;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.service.CandidatoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/Candidato")
@Tag(name = "Candidatos", description = "Consulta e atualização do perfil do candidato")
@SecurityRequirement(name = "bearerAuth")
public class CandidatoController {

    @Autowired
    private CandidatoService candidatoService;

    @Operation(
        summary = "Listar candidatos",
        description = "Retorna todos os candidatos cadastrados no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de candidatos retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @GetMapping("/all")
    public ResponseEntity<List<Candidato>> listarCandidatos(){
        return ResponseEntity.ok(candidatoService.listarTodosOsCandidatos());
    }

    @Operation(
        summary = "Buscar perfil do candidato",
        description = "Retorna os dados de perfil do candidato identificado pelo CPF."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil do candidato encontrado."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Candidato não encontrado.")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<CandidatoProfileDTO> buscarCandidatoPorId(@PathVariable String cpf) {
        try {
            CandidatoProfileDTO dto = candidatoService.buscarPerfilPorCpf(cpf);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Atualizar perfil do candidato",
        description = "Atualiza os dados de perfil do candidato identificado pelo CPF."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Candidato não encontrado.")
    })
    @PutMapping("/{cpf}")
    public ResponseEntity<?> atualizarCandidato(@PathVariable String cpf, @RequestBody @Valid CandidatoProfileDTO dto) {
        try {
            Candidato candidato = candidatoService.atualizarPerfil(cpf, dto);
            return ResponseEntity.ok(candidato);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // @PostMapping
    // public ResponseEntity<Candidato> adicionarCandidato(@Valid @RequestBody Candidato candidato){
    //     return ResponseEntity.status(HttpStatus.CREATED).body(candidatoService.salvarCandidato(candidato));
    // }
}