package com.exemplo.app.controller;

import com.exemplo.app.model.Vaga;
import com.exemplo.app.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}