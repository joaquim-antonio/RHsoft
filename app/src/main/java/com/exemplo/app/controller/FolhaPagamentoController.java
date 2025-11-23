package com.exemplo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.EditarPagamentoDto;
import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.service.FolhaPagamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/folha")
public class FolhaPagamentoController {

    @Autowired
    private FolhaPagamentoService folhaService;

    // ABRIR UMA NOVA FOLHA
    @PostMapping("/abrir")
    public ResponseEntity<FolhaPagamento> abrirFolha(@RequestBody Administrador admin) {
        return ResponseEntity.ok(folhaService.abrirFolha(admin));
    }

    // GERAR PAGAMENTOS (funcionários → pagamentos)
    @PostMapping("/{id}/gerar")
    public ResponseEntity<String> gerarFolha(@PathVariable Long idFolha) {
        folhaService.gerarFolhaDePagamento(idFolha);
        return ResponseEntity.ok("Pagamentos gerados com sucesso.");
    }

    // BUSCAR FOLHA
    @GetMapping("/{id}")
    public ResponseEntity<FolhaPagamento> buscarFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.buscarFolhaPorId(idFolha));
    }

    // EDITAR UM PAGAMENTO (DTO ÚNICO)
    @PutMapping("/pagamento/{codigoPagamento}")
    public ResponseEntity<Pagamento> editarPagamento(@RequestBody @Valid EditarPagamentoDto dto) {
        Pagamento pagamento = folhaService.editarPagamento(dto);
        return ResponseEntity.ok(pagamento);
    }

    // BUSCAR PAGAMENTO PELO CÓDIGO
    @GetMapping("/pagamento/{codigoPagamento}")
    public ResponseEntity<Pagamento> buscarPagamento(@PathVariable String codigo) {
        Pagamento pagamento = folhaService.buscarPagamentoPorCodigo(codigo);
        return ResponseEntity.ok(pagamento);
    }

    // FECHAR FOLHA
    @PostMapping("/{id}/fechar")
    public ResponseEntity<FolhaPagamento> fecharFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.fecharFolha(idFolha));
    }

    // ENVIAR FOLHA PARA OS FUNCIONÁRIOS
    @PostMapping("/{id}/enviar")
    public ResponseEntity<Void> enviarFolhaParaFuncionarios(@PathVariable Long id) {
        folhaService.enviarFolhaParaFuncionarios(id);
        return ResponseEntity.ok().build();
    }
    // REABRIR FOLHA
    @PostMapping("/{id}/reabrir")
    public ResponseEntity<FolhaPagamento> reabrirFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.reabrirFolha(idFolha));
    }

    // CONSOLIDAR FOLHA
    @PostMapping("/{idFolha}/consolidar")
    public ResponseEntity<FolhaPagamento> consolidarFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.consolidarFolha(idFolha));
    }
}
