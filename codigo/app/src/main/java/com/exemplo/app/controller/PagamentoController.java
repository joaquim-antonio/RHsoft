package com.exemplo.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.PagamentoRequestDTO;
import com.exemplo.app.dto.PagamentoResponseDTO;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.service.PagamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagamento")
@Tag(name = "Pagamentos", description = "Pagamentos individuais dos funcionários vinculados a uma folha")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Operation(
        summary = "Criar pagamento",
        description = "Cria um pagamento vinculado a um funcionário e a uma folha de pagamento."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pagamento criado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Funcionário ou folha não encontrados.")
    })
    @PostMapping
    public ResponseEntity<Pagamento> criarPagamento(@RequestBody @Valid PagamentoRequestDTO dto) {
    Pagamento pagamentoParaSalvar = Pagamento.builder()
        .codigo(dto.codigo())
        .cbo(dto.cbo())
        .vencimento(dto.vencimento())
        .mensagens(dto.mensagens())
        .valeAlimentacao(dto.valeAlimentacao())
        .mesAnoReferencia(dto.mesAnoReferencia())
        .itens(dto.itens())
        // vai nulo automaticamente os campos calculados (proventos, descontos, etc) e o service calcula
        .build();

    Pagamento novoPagamento = pagamentoService.criarPagamento(
        pagamentoParaSalvar,
        dto.funcionarioCpf(),
        dto.folhaPagamentoId()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(novoPagamento);
}

    @Operation(
        summary = "Buscar pagamento por código",
        description = "Retorna o pagamento correspondente ao código informado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado.")
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<Pagamento> buscarPagamentoPorCodigo(@PathVariable String codigo) {
        Pagamento pagamento = pagamentoService.buscarPagamentoPorCodigo(codigo);
        return ResponseEntity.ok(pagamento);
    }

    @Operation(
        summary = "Listar pagamentos por funcionário",
        description = "Retorna todos os pagamentos do funcionário identificado pelo CPF."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagamentos retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @GetMapping("/funcionario")
    public ResponseEntity<List<PagamentoResponseDTO>> listarPagamentosPorFuncionario(
            @Parameter(description = "CPF do funcionário", required = true, example = "12345678900") @RequestParam String cpf) {
    List<Pagamento> pagamentos = pagamentoService.listarPagamentosPorFuncionario(cpf);
    
    List<PagamentoResponseDTO> dtos = pagamentos.stream()
        .map(PagamentoResponseDTO::new)
        .toList();

    return ResponseEntity.ok(dtos);
}

    @Operation(
        summary = "Recalcular totais do pagamento",
        description = "Recalcula proventos, descontos e valores líquido do pagamento a partir dos itens informados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Totais recalculados com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado.")
    })
    @PatchMapping("/{codigo}/recalcular")
    public ResponseEntity<Pagamento> recalcularTotais(@PathVariable String codigo) {
        Pagamento pagamentoAtualizado = pagamentoService.recalcularTotais(codigo);
        return ResponseEntity.ok(pagamentoAtualizado);
    }

    @Operation(
        summary = "Excluir pagamento",
        description = "Exclui o pagamento correspondente ao código informado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pagamento excluído com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado.")
    })
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletarPagamento(@PathVariable String codigo) {
        pagamentoService.deletarPagamento(codigo);
        return ResponseEntity.noContent().build();
    }
}
