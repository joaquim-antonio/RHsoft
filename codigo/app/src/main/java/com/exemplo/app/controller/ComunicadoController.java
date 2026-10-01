package com.exemplo.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.ComunicadoResponseDTO;
import com.exemplo.app.model.Comunicado;
import com.exemplo.app.service.ComunicadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/comunicados")
@Tag(name = "Comunicados", description = "Publicação e consulta de comunicados internos")
// Sem @SecurityRequirement de classe: GET /recentes e GET / sao publicos (ver SecurityConfig).
// Os demais metodos declaram @SecurityRequirement individualmente.
public class ComunicadoController {

    @Autowired
    private ComunicadoService comunicadoService;

    @Operation(
        summary = "Listar comunicados recentes",
        description = "Retorna os comunicados mais recentes. Endpoint público."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comunicados recentes retornada com sucesso.")
    })
    @GetMapping("/recentes")
    public ResponseEntity<List<ComunicadoResponseDTO>> listarRecentes() {
        List<ComunicadoResponseDTO> comunicados = comunicadoService.listarRecentes()
                .stream()
                .map(ComunicadoResponseDTO::new)
                .toList();
        return ResponseEntity.ok(comunicados);
    }

    @Operation(
        summary = "Listar comunicados (paginado)",
        description = "Retorna uma página de comunicados, ordenados por data de publicação (mais recentes primeiro). Endpoint público."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de comunicados retornada com sucesso.")
    })
    @GetMapping
    public ResponseEntity<Page<ComunicadoResponseDTO>> listarTodos(
            @Parameter(description = "Parâmetros de paginação e ordenação. Padrão: page=0, size=6, sort=dataPublicacao, direction=DESC")
            @PageableDefault(page = 0, size = 6, sort = "dataPublicacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Comunicado> pageComunicados = comunicadoService.listarPaginado(pageable);
        
        Page<ComunicadoResponseDTO> dtos = pageComunicados.map(ComunicadoResponseDTO::new);
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(
        summary = "Buscar comunicado por ID",
        description = "Retorna o comunicado correspondente ao ID informado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comunicado encontrado."),
        @ApiResponse(responseCode = "404", description = "Comunicado não encontrado.")
    })
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth") // rota protegida: exige token despite GET público das listagens
    public ResponseEntity<ComunicadoResponseDTO> buscarPorId(@PathVariable Long id) {
        Comunicado comunicado = comunicadoService.obterComunicadoPorId(id);
        return ResponseEntity.ok(new ComunicadoResponseDTO(comunicado));
    }

    @Operation(
        summary = "Criar comunicado",
        description = "Publica um novo comunicado. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comunicado criado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @PostMapping
    public ResponseEntity<ComunicadoResponseDTO> criarComunicado(@RequestBody Comunicado comunicado) {
        Comunicado criado = comunicadoService.criarComunicado(comunicado);
        return ResponseEntity.ok(new ComunicadoResponseDTO(criado));
    }

    @Operation(
        summary = "Atualizar comunicado",
        description = "Atualiza o conteúdo de um comunicado existente. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comunicado atualizado com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Comunicado não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ComunicadoResponseDTO> putMethodName(@PathVariable Long id,
            @RequestBody Comunicado comunicado) {
        Comunicado atualizado = comunicadoService.atualizarComunicado(id, comunicado);
        return ResponseEntity.ok(new ComunicadoResponseDTO(atualizado));
    }

    @Operation(
        summary = "Excluir comunicado",
        description = "Exclui um comunicado existente. Restrito a administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comunicado excluído com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso."),
        @ApiResponse(responseCode = "404", description = "Comunicado não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarComunicado(@PathVariable Long id) {
        comunicadoService.deletarComunicado(id);
        return ResponseEntity.noContent().build();
    }
}
