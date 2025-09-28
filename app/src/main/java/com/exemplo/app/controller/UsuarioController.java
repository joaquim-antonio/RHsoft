package com.example.sirius.controller;

// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import java.util.stream.Collectors;

import com.example.sirius.dto.UsuarioDTO;
import com.example.sirius.dto.PageDTO;
import com.example.sirius.model.Usuario;
import com.example.sirius.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private final UsuarioService service;

    @GetMapping
    public PageDTO<UsuarioDTO> search(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) Boolean ativo,
        Pageable pageable
    ){
        Page<Usuario> page = service.search(username, email, ativo, pageable);
        return new PageDTO<>(
            page.getContent().stream().map(Mapper::toDto).collect(Collectors.toList()),
            page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public UsuarioDTO get(@PathVariable Long id){
        return Mapper.toDto(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody UsuarioDTO dto){
        Usuario saved = service.create(Mapper.fromDto(dto), dto.funcionarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public UsuarioDTO update(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto){
        Usuario updated = service.update(id, Mapper.fromDto(dto), dto.funcionarioId());
        return Mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
