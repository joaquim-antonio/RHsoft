package com.exemplo.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados após um login bem-sucedido")
public record LoginResponseDTO(

        @Schema(description = "CPF do usuário autenticado", example = "12345678900")
        String cpf,

        @Schema(description = "Nome do usuário autenticado", example = "João")
        String nome,

        @Schema(description = "Token JWT para uso no header Authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Perfil/role do usuário", example = "ROLE_ADMIN", allowableValues = {"ROLE_ADMIN", "ROLE_USER", "ROLE_CANDIDATO"})
        String role
) {
}
