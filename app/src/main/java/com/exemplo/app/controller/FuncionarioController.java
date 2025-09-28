package com.example.sirius.controller;

// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.example.sirius.dto.FuncionarioDTO;
import com.example.sirius.dto.PageDTO;
import com.example.sirius.model.Funcionario;
import com.example.sirius.service.FuncionarioService;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private final FuncionarioService service;

    @GetMapping
    public PageDTO<FuncionarioDTO> search(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String cpf,
        @RequestParam(required = false) String cargo,
        Pageable pageable
    ){
        Page<Funcionario> page = service.search(nome, cpf, cargo, pageable);
        return new PageDTO<>(
            page.getContent().stream().map(Mapper::toDto).collect(Collectors.toList()),
            page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public FuncionarioDTO get(@PathVariable Long id){
        return Mapper.toDto(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<FuncionarioDTO> create(@Valid @RequestBody FuncionarioDTO dto){
        Funcionario saved = service.create(Mapper.fromDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(Mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public FuncionarioDTO update(@PathVariable Long id, @Valid @RequestBody FuncionarioDTO dto){
        Funcionario updated = service.update(id, Mapper.fromDto(dto));
        return Mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
