package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Endereco;

import io.swagger.v3.oas.annotations.media.Schema;

// DTO para o registro de um novo candidato.
@Schema(description = "Dados para registro de um novo candidato")
public record RegisterCandidatoDTO(

        @Schema(description = "CPF do candidato (sem pontuação)", example = "12345678900")
        String cpf,

        @Schema(description = "Senha de acesso", example = "senha123", format = "password")
        String password,

        @Schema(description = "Nome do candidato", example = "Maria")
        String nome,

        @Schema(description = "Sobrenome do candidato", example = "Silva")
        String sobrenome,

        @Schema(description = "Telefone de contato", example = "31988887777")
        String telefone,

        @Schema(description = "Gênero", example = "FEMININO", allowableValues = {"MASCULINO", "FEMININO", "OUTRO"})
        String sexo,

        @Schema(description = "Data de nascimento", example = "1995-05-20")
        LocalDate dataNascimento,

        @Schema(description = "Endereço do candidato")
        Endereco endereco
) {
}
