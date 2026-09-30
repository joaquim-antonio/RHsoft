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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/Candidato")
public class CandidatoController {

    @Autowired
    private CandidatoService candidatoService;

    @GetMapping("/all")
    public ResponseEntity<List<Candidato>> listarCandidatos(){
        return ResponseEntity.ok(candidatoService.listarTodosOsCandidatos());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<CandidatoProfileDTO> buscarCandidatoPorId(@PathVariable String cpf) {
        try {
            CandidatoProfileDTO dto = candidatoService.buscarPerfilPorCpf(cpf);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

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