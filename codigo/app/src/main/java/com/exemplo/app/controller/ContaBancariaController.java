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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta-bancaria")
@Tag(name = "Conta Bancária", description = "Dados bancários dos funcionários (agência, número, banco e chave PIX)")
@SecurityRequirement(name = "bearerAuth")
public class ContaBancariaController {

    @Autowired
    private ContaBancariaService contaBancariaService;

    @Operation(
        summary = "Cadastrar conta bancária",
        description = "Cadastra os dados bancários de um funcionário."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Conta bancária criada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
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

    @Operation(
        summary = "Buscar conta bancária por ID",
        description = "Retorna a conta bancária correspondente ao ID informado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conta bancária encontrada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContaBancaria> buscarContaBancariaPorId(@PathVariable Long id) {
        ContaBancaria conta = contaBancariaService.buscarContaBancariaPorId(id);
        return ResponseEntity.ok(conta);
    }

    @Operation(
        summary = "Buscar conta bancária por funcionário",
        description = "Retorna a conta bancária do funcionário identificado pelo CPF."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conta bancária encontrada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada.")
    })
    @GetMapping("/funcionario/{cpf}")
    public ResponseEntity<ContaBancaria> buscarContaBancariaPorFuncionario(@PathVariable String cpf) {
        ContaBancaria conta = contaBancariaService.buscarContaBancariaPorFuncionario(cpf);
        return ResponseEntity.ok(conta);
    }

    @Operation(
        summary = "Atualizar conta bancária",
        description = "Atualiza os dados bancários da conta identificada pelo ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conta bancária atualizada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada.")
    })
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

    @Operation(
        summary = "Atualizar chave PIX",
        description = "Atualiza apenas a chave PIX da conta bancária identificada pelo ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chave PIX atualizada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada.")
    })
    @PatchMapping("/{id}/chave-pix")
    public ResponseEntity<ContaBancaria> atualizarChavePix(@PathVariable Long id, @RequestBody String novaChavePix) {
        ContaBancaria contaAtualizada = contaBancariaService.atualizarChavePix(id, novaChavePix);
        return ResponseEntity.ok(contaAtualizada);
    }

    @Operation(
        summary = "Excluir conta bancária",
        description = "Exclui a conta bancária identificada pelo ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta bancária excluída com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarContaBancaria(@PathVariable Long id) {
        contaBancariaService.deletarContaBancaria(id);
        return ResponseEntity.noContent().build();
    }
}
