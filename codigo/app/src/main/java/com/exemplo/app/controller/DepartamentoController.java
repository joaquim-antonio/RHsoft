package com.exemplo.app.controller;

import com.exemplo.app.model.Departamento;
import com.exemplo.app.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/departamento")
@RestController
@Tag(name = "Departamentos", description = "Gestão dos departamentos da organização")
@SecurityRequirement(name = "bearerAuth")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @Autowired
    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    // GET - Listar todos
    @Operation(
        summary = "Listar departamentos",
        description = "Retorna todos os departamentos cadastrados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de departamentos retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @GetMapping
    public ResponseEntity<List<Departamento>> listarTodosDepartamentos() {
        List<Departamento> departamentos = departamentoService.listarTodosDepartamentos();
        return new ResponseEntity<>(departamentos, HttpStatus.OK);
    }

    // GET - Buscar por código
    @Operation(
        summary = "Buscar departamento por código",
        description = "Retorna o departamento correspondente ao código informado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Departamento encontrado."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Departamento não encontrado.")
    })
    @GetMapping(path = "/{codigo}")
    public ResponseEntity<?> buscarDepartamentoPorCodigo(@PathVariable Long codigo) {
        try {
            Departamento departamento = departamentoService.buscarDepartamentoPorCodigo(codigo);
            return new ResponseEntity<>(departamento, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // POST - Criar novo
    @Operation(
        summary = "Criar departamento",
        description = "Cadastra um novo departamento."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Departamento criado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já utilizado."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @PostMapping
    public ResponseEntity<?> criarDepartamento(@RequestBody @Valid Departamento departamento) {
        try {
            Departamento departamentoEmCriacao = departamentoService.criarDepartamento(departamento);
            return new ResponseEntity<>(departamentoEmCriacao, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar
    @Operation(
        summary = "Atualizar departamento",
        description = "Atualiza os dados do departamento identificado pelo código."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Departamento atualizado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Departamento não encontrado.")
    })
    @PutMapping(path = "/{codigo}")
    public ResponseEntity<?> atualizarDepartamento(@PathVariable Long codigo, @RequestBody @Valid Departamento departamentoAtualizado) {
        try {
            Departamento novoDepartamento = departamentoService.atualizarDepartamento(codigo, departamentoAtualizado);
            return new ResponseEntity<>(novoDepartamento, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Excluir
    @Operation(
        summary = "Excluir departamento",
        description = "Exclui o departamento identificado pelo código."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Departamento excluído com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Departamento não encontrado."),
        @ApiResponse(responseCode = "409", description = "Departamento possui funcionários vinculados e não pode ser excluído.")
    })
    @DeleteMapping(path = "/{codigo}")
    public ResponseEntity<?> deletarDepartamento(@PathVariable Long codigo) {
        try {
            departamentoService.deletarDepartamento(codigo);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
