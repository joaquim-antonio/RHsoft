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
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.service.PagamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

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

    @GetMapping("/{codigo}")
    public ResponseEntity<Pagamento> buscarPagamentoPorCodigo(@PathVariable String codigo) {
        Pagamento pagamento = pagamentoService.buscarPagamentoPorCodigo(codigo);
        return ResponseEntity.ok(pagamento);
    }

    @GetMapping("/funcionario")
    public ResponseEntity<List<Pagamento>> listarPagamentosPorFuncionario(@RequestParam String cpf) {
        List<Pagamento> pagamentos = pagamentoService.listarPagamentosPorFuncionario(cpf);
        return ResponseEntity.ok(pagamentos);
    }

    @PatchMapping("/{codigo}/recalcular")
    public ResponseEntity<Pagamento> recalcularTotais(@PathVariable String codigo) {
        Pagamento pagamentoAtualizado = pagamentoService.recalcularTotais(codigo);
        return ResponseEntity.ok(pagamentoAtualizado);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletarPagamento(@PathVariable String codigo) {
        pagamentoService.deletarPagamento(codigo);
        return ResponseEntity.noContent().build();
    }
}
