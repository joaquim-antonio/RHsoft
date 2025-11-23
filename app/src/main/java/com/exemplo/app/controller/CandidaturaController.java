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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/candidaturas")
public class CandidaturaController {

    @Autowired
    private CandidaturaService candidaturaService;

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
    @GetMapping("/minhas")
    public ResponseEntity<List<CandidaturaResponseDTO>> minhasCandidaturas(Authentication authentication) {
        String cpfLogado = authentication.getName();
        
        List<CandidaturaResponseDTO> dtos = candidaturaService.listarMinhasCandidaturas(cpfLogado)
                .stream()
                .map(CandidaturaResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/vaga/{vagaId}")
    public ResponseEntity<List<CandidaturaResponseDTO>> verCandidatosPorVaga(@PathVariable Long vagaId) {
        
        List<CandidaturaResponseDTO> dtos = candidaturaService.listarCandidatosDaVaga(vagaId)
                .stream()
                .map(CandidaturaResponseDTO::new)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

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