package com.exemplo.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.UserResponseDTO;
import com.exemplo.app.model.Pessoa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user")
@Tag(name = "Usuário", description = "Dados do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Operation(
        summary = "Obter dados do usuário logado",
        description = "Retorna as informações básicas do usuário autenticado (CPF, nome, sobrenome e perfil)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados do usuário retornados com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getMyUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }

        Pessoa pessoaLogada = (Pessoa) authentication.getPrincipal();

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        UserResponseDTO dto = new UserResponseDTO(
                pessoaLogada.getCpf(),
                pessoaLogada.getNome(),
                pessoaLogada.getSobrenome(),
                role);
        return ResponseEntity.ok(dto);
    }
}