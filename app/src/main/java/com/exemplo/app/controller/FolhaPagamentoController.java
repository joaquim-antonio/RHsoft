package com.exemplo.app.controller;

import com.exemplo.app.dto.FolhaPagamentoDto;
import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.service.FolhaPagamentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/folha")
public class FolhaPagamentoController {

    @Autowired
    private FolhaPagamentoService folhaService;

    //   ABRIR UMA NOVA FOLHA
    @PostMapping("/abrir")
    public ResponseEntity<FolhaPagamento> abrirFolha(@RequestBody Administrador admin) {
        return ResponseEntity.ok(folhaService.abrirFolha(admin));
    }

    //   GERAR PAGAMENTOS (funcionários → pagamentos)
    @PostMapping("/{idFolha}/gerar")
    public ResponseEntity<String> gerarFolha(@PathVariable Long idFolha) {
        folhaService.gerarFolhaDePagamento(idFolha);
        return ResponseEntity.ok("Pagamentos gerados com sucesso.");
    }

    //  BUSCAR FOLHA
    @GetMapping("/{idFolha}")
    public ResponseEntity<FolhaPagamento> buscarFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.buscarFolhaPorId(idFolha));
    }

    //   EDITAR UM PAGAMENTO (DTO ÚNICO)
    @PutMapping("/pagamento/{codigoPagamento}")
    public ResponseEntity<FolhaPagamentoDto> editarPagamento(
            @PathVariable String codigoPagamento,
            @RequestBody FolhaPagamentoDto dto) {

        return ResponseEntity.ok(folhaService.editarPagamento(codigoPagamento, dto));
    }

    //   BUSCAR PAGAMENTO PELO CÓDIGO
    @GetMapping("/pagamento/{codigoPagamento}")
    public ResponseEntity<FolhaPagamentoDto> buscarPagamento(@PathVariable String codigoPagamento) {
        return ResponseEntity.ok(folhaService.buscarPagamentoDto(codigoPagamento));
    }

    //  FECHAR FOLHA
    @PostMapping("/{idFolha}/fechar")
    public ResponseEntity<FolhaPagamento> fecharFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.fecharFolha(idFolha));
    }

    //  ENVIAR FOLHA PARA OS FUNCIONÁRIOS
    @PostMapping("/{idFolha}/enviar")
    public ResponseEntity<FolhaPagamento> enviarFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.enviarFolhaParaFuncionarios(idFolha));
    }

    //   REABRIR FOLHA
    @PostMapping("/{idFolha}/reabrir")
    public ResponseEntity<FolhaPagamento> reabrirFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.reabrirFolha(idFolha));
    }

    //  CONSOLIDAR FOLHA
    @PostMapping("/{idFolha}/consolidar")
    public ResponseEntity<FolhaPagamento> consolidarFolha(@PathVariable Long idFolha) {
        return ResponseEntity.ok(folhaService.consolidarFolha(idFolha));
    }
}
