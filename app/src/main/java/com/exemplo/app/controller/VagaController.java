package com.exemplo.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.RequestVagaDTO;
import com.exemplo.app.dto.VagaCandidatoDTO;
import com.exemplo.app.dto.VagaResponseDTO;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.service.VagaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/vagas")
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @GetMapping("/disponiveis")
    public ResponseEntity<List<VagaCandidatoDTO>> listarVagasDisponiveis() {
        
        List<VagaCandidatoDTO> vagas = vagaService.listarVagasDisponiveis().stream()
                .map(VagaCandidatoDTO::new) 
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(vagas);
    }

    @GetMapping
    public ResponseEntity<List<VagaResponseDTO>> listarTodasVagas() {
        List<VagaResponseDTO> vagas = vagaService.listarTodasVagas().stream()
                .map(VagaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vagas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VagaResponseDTO> buscarVagaPorId(@PathVariable Long id) {
        Vaga vaga = vagaService.buscarVagaPorId(id);
        return ResponseEntity.ok(new VagaResponseDTO(vaga));
    }

    @PreAuthorize("hasRole('ADMIN')") 
    @PostMapping
    public ResponseEntity<VagaResponseDTO> criarVaga(@RequestBody @Valid RequestVagaDTO vagaDto) {
        Vaga vagaCriada = vagaService.criarVaga(vagaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VagaResponseDTO(vagaCriada));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VagaResponseDTO> editarVaga(@PathVariable Long id, @RequestBody @Valid RequestVagaDTO vagaAtualizada) {
        Vaga vagaEditada = vagaService.atualizarVaga(id, vagaAtualizada);
        return ResponseEntity.ok(new VagaResponseDTO(vagaEditada));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVaga(@PathVariable Long id) {
        try {
            vagaService.excluirVaga(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // Retorna erro 409 Conflict se houver candidatos inscritos
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}