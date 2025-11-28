package com.exemplo.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.EditarPagamentoDto;
import com.exemplo.app.dto.FolhaPagamentoResponseDto;
import com.exemplo.app.dto.PagamentoResponseDTO;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.service.FolhaPagamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/folha-pagamento")
public class FolhaPagamentoController {

    @Autowired
    private FolhaPagamentoService folhaService;

    // LISTAR
    @GetMapping("/listar")
    public ResponseEntity<List<FolhaPagamentoResponseDto>> listarTodas() {
        List<FolhaPagamento> folhas = folhaService.listarTodas();   
        List<FolhaPagamentoResponseDto> dtos = folhas.stream()
            .map(FolhaPagamentoResponseDto::fromEntity)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(dtos);
    }

    // ABRIR 
    @PostMapping("/abrir")
    public ResponseEntity<FolhaPagamentoResponseDto> abrirFolha() {
        String cpfAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        FolhaPagamento folhaAberta = folhaService.abrirFolha(cpfAdmin);
        
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folhaAberta));
    }

    // BUSCAR FOLHA POR ID
    @GetMapping("/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> buscarFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.buscarFolhaPorId(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // EDITAR PAGAMENTO
    @PutMapping("/pagamento/editar")
    public ResponseEntity<PagamentoResponseDTO> editarPagamento(@RequestBody @Valid EditarPagamentoDto dto) {
        Pagamento pagamento = folhaService.editarPagamento(dto);
        return ResponseEntity.ok(new PagamentoResponseDTO(pagamento));
    }

    // FECHAR FOLHA
    @PostMapping("/fechar/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> fecharFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.fecharFolha(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // CONSOLIDAR FOLHA 
    @PostMapping("/consolidar/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> consolidarFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.consolidarFolha(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // REABRIR FOLHA 
    @PostMapping("/reabrir/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> reabrirFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.reabrirFolha(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // GERAR
    @PostMapping("/gerar/{idFolha}")
    public ResponseEntity<String> gerarFolha(@PathVariable Long idFolha) {
        folhaService.gerarFolhaDePagamento(idFolha);
        return ResponseEntity.ok("Pagamentos gerados com sucesso.");
    }
    
    // ENVIAR (Retorna Void)
    @PostMapping("/{idFolha}/enviar")
    public ResponseEntity<Void> enviarFolhaParaFuncionarios(@PathVariable Long idFolha) {
        folhaService.enviarFolhaParaFuncionarios(idFolha);
        return ResponseEntity.ok().build();
    }
}
