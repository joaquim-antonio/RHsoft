package com.exemplo.app.controller;

import java.util.List;

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

import com.exemplo.app.model.Cargo;
import com.exemplo.app.service.CargoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cargo")
@Tag(name = "Cargos", description = "Gestão de cargos/ocupações (base CBO 2002)")
@SecurityRequirement(name = "bearerAuth")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    @Operation(
        summary = "Listar todos os cargos",
        description = "Retorna todos os cargos cadastrados. Os cargos são carregados automaticamente na inicialização a partir da base CBO 2002."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cargos retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @GetMapping
    public ResponseEntity<List<Cargo>> listarTodos() {
        List<Cargo> cargos = cargoService.listarTodosCargos();
        return ResponseEntity.ok(cargos);
    }

    @Operation(
        summary = "Buscar cargo por código",
        description = "Retorna o cargo correspondente ao código informado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cargo encontrado."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Cargo não encontrado.")
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<Cargo> buscarPorId(@PathVariable Long codigo) {
        Cargo cargo = cargoService.buscarCargoPorCodigo(codigo);
        return ResponseEntity.ok(cargo);
    }

    @Operation(
        summary = "Criar cargo",
        description = "Cadastra um novo cargo. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cargo criado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Cargo> criarCargo(@RequestBody @Valid Cargo cargo) {
        Cargo novoCargo = cargoService.criarCargo(cargo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCargo);
    }

    @Operation(
        summary = "Atualizar cargo",
        description = "Atualiza os dados do cargo identificado pelo código. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cargo atualizado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Cargo não encontrado.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{codigo}")
    public ResponseEntity<Cargo> atualizarCargo(@PathVariable Long codigo, @RequestBody @Valid Cargo cargo) {
        Cargo atualizado = cargoService.atualizarCargo(codigo, cargo);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(
        summary = "Excluir cargo",
        description = "Exclui o cargo identificado pelo código. Restrito a administradores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cargo excluído com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "409", description = "Cargo em uso por algum funcionário e não pode ser excluído.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletarCargo(@PathVariable Long codigo) {
        cargoService.deletarCargo(codigo);
        return ResponseEntity.noContent().build();
    }
}