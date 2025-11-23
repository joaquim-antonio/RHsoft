package com.exemplo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.ContaBancariaRequestDTO;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.service.ContaBancariaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta-bancaria")
public class ContaBancariaController {

    @Autowired
    private ContaBancariaService contaBancariaService;

    @PostMapping
    public ResponseEntity<ContaBancaria> criarContaBancaria(@RequestBody @Valid ContaBancariaRequestDTO dto) {
        ContaBancaria novaConta = contaBancariaService.criarContaBancaria(
            new ContaBancaria(
                dto.agencia(),
                dto.numero(),
                dto.nomeBanco(),
                dto.chavePix()
            ),
            dto.funcionarioCpf()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaBancaria> buscarContaBancariaPorId(@PathVariable Long id) {
        ContaBancaria conta = contaBancariaService.buscarContaBancariaPorId(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/funcionario/{cpf}")
    public ResponseEntity<ContaBancaria> buscarContaBancariaPorFuncionario(@PathVariable String cpf) {
        ContaBancaria conta = contaBancariaService.buscarContaBancariaPorFuncionario(cpf);
        return ResponseEntity.ok(conta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaBancaria> atualizarContaBancaria(@PathVariable Long id, @RequestBody @Valid ContaBancariaRequestDTO dto) {
        ContaBancaria contaAtualizada = contaBancariaService.atualizarContaBancaria(
            id,
            dto.agencia(),
            dto.numero(),
            dto.nomeBanco(),
            dto.chavePix(),
            dto.funcionarioCpf()
        );
        return ResponseEntity.ok(contaAtualizada);
    }

    @PatchMapping("/{id}/chave-pix")
    public ResponseEntity<ContaBancaria> atualizarChavePix(@PathVariable Long id, @RequestBody String novaChavePix) {
        ContaBancaria contaAtualizada = contaBancariaService.atualizarChavePix(id, novaChavePix);
        return ResponseEntity.ok(contaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarContaBancaria(@PathVariable Long id) {
        contaBancariaService.deletarContaBancaria(id);
        return ResponseEntity.noContent().build();
    }
}
