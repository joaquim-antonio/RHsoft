package com.exemplo.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.RequestVagaDTO;
import com.exemplo.app.dto.VagaCandidatoDTO;
import com.exemplo.app.dto.VagaResponseDTO;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.service.VagaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/vagas")
@Tag(name = "Vagas", description = "Publicação e gestão de vagas de emprego")
// Sem @SecurityRequirement de classe: /disponiveis e /{id} sao publicas (ver SecurityConfig).
// Os demais metodos declaram @SecurityRequirement individualmente.
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @Operation(
        summary = "Listar vagas disponíveis",
        description = "Retorna as vagas abertas ao público, sem necessidade de autenticação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de vagas disponíveis retornada com sucesso.")
    })
    @GetMapping("/disponiveis")
    public ResponseEntity<List<VagaCandidatoDTO>> listarVagasDisponiveis() {
        
        List<VagaCandidatoDTO> vagas = vagaService.listarVagasDisponiveis().stream()
                .map(VagaCandidatoDTO::new) 
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(vagas);
    }

    @Operation(
        summary = "Listar todas as vagas",
        description = "Retorna todas as vagas, incluindo as fechadas. Restrito a administradores e usuários autorizados."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @GetMapping
    public ResponseEntity<List<VagaResponseDTO>> listarTodasVagas() {
        List<VagaResponseDTO> vagas = vagaService.listarTodasVagas().stream()
                .map(VagaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vagas);
    }

    @Operation(
        summary = "Buscar vaga por ID",
        description = "Retorna a vaga correspondente ao ID informado. Endpoint público."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vaga encontrada."),
        @ApiResponse(responseCode = "404", description = "Vaga não encontrada.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VagaResponseDTO> buscarVagaPorId(@PathVariable Long id) {
        Vaga vaga = vagaService.buscarVagaPorId(id);
        return ResponseEntity.ok(new VagaResponseDTO(vaga));
    }

    @Operation(
        summary = "Publicar vaga",
        description = "Cria e publica uma nova vaga. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VagaResponseDTO> criarVaga(@RequestBody @Valid RequestVagaDTO vagaDto) {
        Vaga vagaCriada = vagaService.criarVaga(vagaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VagaResponseDTO(vagaCriada));
    }

    @Operation(
        summary = "Editar vaga",
        description = "Atualiza os dados da vaga identificada pelo ID. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vaga atualizada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Vaga não encontrada.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VagaResponseDTO> editarVaga(@PathVariable Long id, @RequestBody @Valid RequestVagaDTO vagaAtualizada) {
        Vaga vagaEditada = vagaService.atualizarVaga(id, vagaAtualizada);
        return ResponseEntity.ok(new VagaResponseDTO(vagaEditada));
    }

    @Operation(
        summary = "Excluir vaga",
        description = "Exclui a vaga identificada pelo ID. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vaga excluída com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Vaga não encontrada."),
        @ApiResponse(responseCode = "409", description = "Vaga possui candidatos inscritos e não pode ser excluída.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVaga(@PathVariable Long id) {
        try {
            vagaService.excluirVaga(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            // Retorna erro 409 Conflict se houver candidatos inscritos
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}