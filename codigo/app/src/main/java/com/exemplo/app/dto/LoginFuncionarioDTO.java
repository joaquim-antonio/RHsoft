package com.exemplo.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais de login")
public record LoginFuncionarioDTO(

        @Schema(description = "CPF do usuário (sem pontuação)", example = "12345678900")
        String cpf,

        @Schema(description = "Senha do usuário", example = "senha123", format = "password")
        String password
) {
}
