package com.exemplo.app.controller;

import com.exemplo.app.model.Departamento;
import com.exemplo.app.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/departamento")
@RestController
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @Autowired
    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    // GET - Listar todos
    @GetMapping
    public ResponseEntity<List<Departamento>> listarTodosDepartamentos() {
        List<Departamento> departamentos = departamentoService.listarTodosDepartamentos();
        return new ResponseEntity<>(departamentos, HttpStatus.OK);
    }

    // GET - Buscar por código
    @GetMapping(path = "/{codigo}")
    public ResponseEntity<?> buscarDepartamentoPorCodigo(@PathVariable Long codigo) {
        try {
            Departamento departamento = departamentoService.buscarDepartamentoPorCodigo(codigo);
            return new ResponseEntity<>(departamento, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // POST - Criar novo
    @PostMapping
    public ResponseEntity<?> criarDepartamento(@RequestBody @Valid Departamento departamento) {
        try {
            Departamento departamentoEmCriacao = departamentoService.criarDepartamento(departamento);
            return new ResponseEntity<>(departamentoEmCriacao, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar
    @PutMapping(path = "/{codigo}")
    public ResponseEntity<?> atualizarDepartamento(@PathVariable Long codigo, @RequestBody @Valid Departamento departamentoAtualizado) {
        try {
            Departamento novoDepartamento = departamentoService.atualizarDepartamento(codigo, departamentoAtualizado);
            return new ResponseEntity<>(novoDepartamento, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Excluir
    @DeleteMapping(path = "/{codigo}")
    public ResponseEntity<?> deletarDepartamento(@PathVariable Long codigo) {
        try {
            departamentoService.deletarDepartamento(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
