package com.exemplo.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.AtualizarStatusDTO;
import com.exemplo.app.dto.CandidaturaResponseDTO;
import com.exemplo.app.dto.DadosContratacaoDTO;
import com.exemplo.app.model.Candidatura;
import com.exemplo.app.service.CandidaturaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/candidaturas")
@Tag(name = "Candidaturas", description = "Candidaturas de candidatos às vagas de emprego")
@SecurityRequirement(name = "bearerAuth")
public class CandidaturaController {

    @Autowired
    private CandidaturaService candidaturaService;

    @Operation(
        summary = "Aplicar para uma vaga",
        description = "Registra a candidatura do candidato logado à vaga informada. Apenas candidatos podem se candidatar."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Candidatura registrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Candidato já candidatou-se a esta vaga ou a vaga não está disponível."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "500", description = "Erro interno ao registrar a candidatura.")
    })
    @PostMapping("/aplicar/{vagaId}")
    public ResponseEntity<?> aplicarParaVaga(@PathVariable Long vagaId, Authentication authentication) {
        try {
            String cpfLogado = authentication.getName();
            
            Candidatura candidatura = candidaturaService.aplicarParaVaga(cpfLogado, vagaId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new CandidaturaResponseDTO(candidatura));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao aplicar: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Cancelar candidatura",
        description = "Cancela a candidatura do candidato logado à vaga informada."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Candidatura cancelada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Candidatura não encontrada ou já cancelada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @DeleteMapping("/cancelar/{vagaId}")
    public ResponseEntity<?> cancelarCandidatura(@PathVariable Long vagaId, Authentication authentication) {
        String cpfLogado = authentication.getName();
        
        try {
            candidaturaService.cancelarCandidatura(vagaId, cpfLogado);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cancelar candidatura: " + e.getMessage());
        }
    }
    @Operation(
        summary = "Listar minhas candidaturas",
        description = "Retorna todas as candidaturas do candidato logado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de candidaturas retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @GetMapping("/minhas")
    public ResponseEntity<List<CandidaturaResponseDTO>> minhasCandidaturas(Authentication authentication) {
        String cpfLogado = authentication.getName();
        
        List<CandidaturaResponseDTO> dtos = candidaturaService.listarMinhasCandidaturas(cpfLogado)
                .stream()
                .map(CandidaturaResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(
        summary = "Listar candidatos de uma vaga",
        description = "Retorna todas as candidaturas registradas para a vaga informada. Acesso restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de candidatos da vaga retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @GetMapping("/vaga/{vagaId}")
    public ResponseEntity<List<CandidaturaResponseDTO>> verCandidatosPorVaga(@PathVariable Long vagaId) {
        
        List<CandidaturaResponseDTO> dtos = candidaturaService.listarCandidatosDaVaga(vagaId)
                .stream()
                .map(CandidaturaResponseDTO::new)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @Operation(
        summary = "Atualizar status da candidatura",
        description = "Atualiza o status de uma candidatura (ex.: APROVADA, REJEITADA, EM_ANALISE)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Transição de status inválida."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id, 
            @RequestBody @Valid AtualizarStatusDTO dto) {
        try {
            Candidatura candidaturaAtualizada = candidaturaService.atualizarStatus(id, dto.status());
            return ResponseEntity.ok(new CandidaturaResponseDTO(candidaturaAtualizada));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Aprovar candidatura e contratar",
        description = "Aprova a candidatura e efetua a contratação do candidato, criando o funcionário correspondente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Candidatura aprovada e contratação realizada."),
        @ApiResponse(responseCode = "400", description = "Regra de negócio violada (ex.: candidatura não está em status aprovável)."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "500", description = "Erro interno na contratação.")
    })
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarEContratar(
            @PathVariable Long id, 
            @RequestBody @Valid DadosContratacaoDTO dados) {
        try {
            Candidatura candidatura = candidaturaService.aprovarEContratar(id, dados);
            
            return ResponseEntity.ok(new CandidaturaResponseDTO(candidatura));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na contratação: " + e.getMessage());
        }
    }
}