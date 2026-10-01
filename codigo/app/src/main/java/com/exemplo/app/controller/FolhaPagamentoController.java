package com.exemplo.app.controller;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/folha-pagamento")
@Tag(name = "Folha de Pagamento", description = "Ciclo de vida da folha mensal: abrir, gerar, consolidar, fechar, reabrir e enviar")
@SecurityRequirement(name = "bearerAuth")
public class FolhaPagamentoController {

    @Autowired
    private FolhaPagamentoService folhaService;

    // LISTAR
    @Operation(
        summary = "Listar todas as folhas de pagamento",
        description = "Retorna todas as folhas de pagamento cadastradas. Restrito a administradores/usuários autorizados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Folhas listadas com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<FolhaPagamentoResponseDto>> listarTodas() {
        List<FolhaPagamentoResponseDto> dtos = folhaService.listarTodasDTO();   
            
        return ResponseEntity.ok(dtos);
    }

    // ABRIR 
    @Operation(
        summary = "Abrir folha de pagamento",
        description = "Abre uma nova folha de pagamento para o mês de referência atual. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Folha aberta com sucesso."),
        @ApiResponse(responseCode = "400", description = "Já existe folha aberta para o período."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @PostMapping("/abrir")
    public ResponseEntity<FolhaPagamentoResponseDto> abrirFolha() {
        String cpfAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        FolhaPagamento folhaAberta = folhaService.abrirFolha(cpfAdmin);
        
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folhaAberta));
    }

    // BUSCAR FOLHA POR ID
    @Operation(
        summary = "Buscar folha por ID",
        description = "Retorna a folha de pagamento correspondente ao ID informado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Folha encontrada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Folha não encontrada.")
    })
    @GetMapping("/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> buscarFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.buscarFolhaPorId(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // EDITAR PAGAMENTO
    @Operation(
        summary = "Editar pagamento",
        description = "Edita os itens de um pagamento existente dentro da folha. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento editado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado.")
    })
    @PutMapping("/pagamento/editar")
    public ResponseEntity<PagamentoResponseDTO> editarPagamento(@RequestBody @Valid EditarPagamentoDto dto) {
        Pagamento pagamento = folhaService.editarPagamento(dto);
        return ResponseEntity.ok(new PagamentoResponseDTO(pagamento));
    }

    // FECHAR FOLHA
    @Operation(
        summary = "Fechar folha de pagamento",
        description = "Fecha a folha após a geração dos pagamentos. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Folha fechada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Regra de negócio violada (ex.: folha não está no estado correto)."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Folha não encontrada.")
    })
    @PostMapping("/fechar/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> fecharFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.fecharFolha(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // CONSOLIDAR FOLHA 
    @Operation(
        summary = "Consolidar folha de pagamento",
        description = "Consolida a folha consolidando os pagamentos gerados. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Folha consolidada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Regra de negócio violada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Folha não encontrada.")
    })
    @PostMapping("/consolidar/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> consolidarFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.consolidarFolha(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // REABRIR FOLHA 
    @Operation(
        summary = "Reabrir folha de pagamento",
        description = "Reabre uma folha fechada para permitir alterações. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Folha reaberta com sucesso."),
        @ApiResponse(responseCode = "400", description = "Regra de negócio violada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Folha não encontrada.")
    })
    @PostMapping("/reabrir/{idFolha}")
    public ResponseEntity<FolhaPagamentoResponseDto> reabrirFolha(@PathVariable Long idFolha) {
        FolhaPagamento folha = folhaService.reabrirFolha(idFolha);
        return ResponseEntity.ok(FolhaPagamentoResponseDto.fromEntity(folha));
    }

    // GERAR
    @Operation(
        summary = "Gerar pagamentos da folha",
        description = "Gera os pagamentos de todos os funcionários vinculados à folha. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamentos gerados com sucesso."),
        @ApiResponse(responseCode = "400", description = "Regra de negócio violada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Folha não encontrada.")
    })
    @PostMapping("/gerar/{idFolha}")
    public ResponseEntity<String> gerarFolha(@PathVariable Long idFolha) {
        folhaService.gerarFolhaDePagamento(idFolha);
        return ResponseEntity.ok("Pagamentos gerados com sucesso.");
    }
    
    // ENVIAR (Retorna Void)
    @Operation(
        summary = "Enviar folha para os funcionários",
        description = "Envia a folha de pagamento para os funcionários vinculados, liberando os contracheques. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Folha enviada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Folha não encontrada.")
    })
    @PostMapping("/{idFolha}/enviar")
    public ResponseEntity<Void> enviarFolhaParaFuncionarios(@PathVariable Long idFolha) {
        folhaService.enviarFolhaParaFuncionarios(idFolha);
        return ResponseEntity.ok().build();
    }
}
