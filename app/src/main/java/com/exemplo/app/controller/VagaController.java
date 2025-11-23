package com.exemplo.app.controller;

import com.exemplo.app.dto.RequestVagaDTO;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vagas")
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @GetMapping
    public ResponseEntity<List<Vaga>> listarTodasVagas() {
        List<Vaga> vagas = vagaService.listarTodasVagas();
        return ResponseEntity.ok(vagas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> buscarVagaPorId(@PathVariable Long id) {
        Vaga vaga = vagaService.buscarVagaPorId(id);
        return ResponseEntity.ok(vaga);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Vaga> criarVaga(@RequestBody RequestVagaDTO vagaDto){
        Vaga vagaCriada = vagaService.criarVaga(vagaDto);
        return ResponseEntity.ok(vagaCriada);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Vaga> editarVaga(@PathVariable Long id, @RequestBody RequestVagaDTO vagaAtualizada){
        Vaga vagaCriada = vagaService.atualizarVaga(id, vagaAtualizada);
        return ResponseEntity.ok(vagaCriada);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVaga(@PathVariable Long id){
        vagaService.excluirVaga(id);
        return ResponseEntity.ok().build();
    }

}