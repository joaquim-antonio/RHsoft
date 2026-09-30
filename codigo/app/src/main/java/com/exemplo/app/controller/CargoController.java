package com.exemplo.app.controller;

import java.util.List;

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

import com.exemplo.app.model.Cargo;
import com.exemplo.app.service.CargoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cargo")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    @GetMapping
    public ResponseEntity<List<Cargo>> listarTodos() {
        List<Cargo> cargos = cargoService.listarTodosCargos();
        return ResponseEntity.ok(cargos);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Cargo> buscarPorId(@PathVariable Long codigo) {
        Cargo cargo = cargoService.buscarCargoPorCodigo(codigo);
        return ResponseEntity.ok(cargo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Cargo> criarCargo(@RequestBody @Valid Cargo cargo) {
        Cargo novoCargo = cargoService.criarCargo(cargo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCargo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{codigo}")
    public ResponseEntity<Cargo> atualizarCargo(@PathVariable Long codigo, @RequestBody @Valid Cargo cargo) {
        Cargo atualizado = cargoService.atualizarCargo(codigo, cargo);
        return ResponseEntity.ok(atualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletarCargo(@PathVariable Long codigo) {
        cargoService.deletarCargo(codigo);
        return ResponseEntity.noContent().build();
    }
}