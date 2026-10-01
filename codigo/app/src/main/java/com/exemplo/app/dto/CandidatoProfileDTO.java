package com.exemplo.app.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Perfil do candidato")
public record CandidatoProfileDTO(

    @Schema(description = "CPF do candidato", example = "12345678900")
    String cpf,

    @Schema(description = "Nome do candidato", example = "Maria")
    String nome,

    @Schema(description = "Sobrenome do candidato", example = "Silva")
    String sobrenome,

    @Schema(description = "Telefone de contato", example = "31988887777")
    String telefone,

    // String email, Placeholder
    @Schema(description = "Habilidades do candidato", example = "[\"Java\", \"SQL\"]")
    List<String> habilidades,

    @Schema(description = "Formação acadêmica do candidato", example = "[\"Bacharelado em Sistemas de Informação\"]")
    List<String> formacao,

    @Schema(description = "Experiências profissionais do candidato", example = "[\"Analista de Sistemas - 2 anos\"]")
    List<String> experiencias
) {}
