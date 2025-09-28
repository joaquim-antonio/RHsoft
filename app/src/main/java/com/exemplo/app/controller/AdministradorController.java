package com.exemplo.app.controller;

import com.exemplo.app.dto.AdministradorDto;
import com.exemplo.app.mapper.AdministradorMapper;
import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.service.AdministradorService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDto> buscarPorId(@PathVariable Long id) {
        Administrador administrador = administradorService.buscarPorId(id);
        AdministradorDto dto = AdministradorMapper.toDTO(administrador);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    public ResponseEntity<List<AdministradorDto>> listarTodos() {
        List<Administrador> administradores = administradorService.listarTodos();
        
        List<AdministradorDto> dtos = administradores.stream()
                .map(AdministradorMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<FuncionarioDto> contratarCandidato(@PathVariable Long candidatoId){
        Funcionario novoFuncionario = administradorService.contratarFuncionario(candidatoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(FuncionarioMapper.toDTO(novoFuncionario));
    }

    @PostMapping
    public ResponseEntity<FolhaPagamentoDto> fecharFolhaDePagamento(@PathVariable Long folhaPagamentoId) {
        FolhaPagamento folhaFechada = administradorService.fecharFolhaPagamento(folhaPagamentoId);
        return ResponseEntity.ok(FolhaPagamentoMapper.toDTO(folhaFechada));
    }

    @DeleteMapping
    public ResponseEntity<Void> demitirFuncionario(@PathVariable Long funcionarioId) {
        administradorService.demitirFuncionario(funcionarioId);
        return ResponseEntity.noContent().build();
    }