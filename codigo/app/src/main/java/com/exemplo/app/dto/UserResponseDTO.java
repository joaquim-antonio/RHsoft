package com.exemplo.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do usuário autenticado")
public record UserResponseDTO(

    @Schema(description = "CPF do usuário", example = "12345678900")
    String cpf,

    @Schema(description = "Nome do usuário", example = "João")
    String nome,

    @Schema(description = "Sobrenome do usuário", example = "Souza")
    String sobrenome,

    @Schema(description = "Perfil/role do usuário", example = "ROLE_ADMIN", allowableValues = {"ROLE_ADMIN", "ROLE_USER", "ROLE_CANDIDATO"})
    String role
) {}
