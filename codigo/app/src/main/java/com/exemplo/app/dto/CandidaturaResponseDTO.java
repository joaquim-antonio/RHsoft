package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Candidatura;
import com.exemplo.app.model.Enums.StatusCandidatura;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma candidatura")
public record CandidaturaResponseDTO(

        @Schema(description = "Código da candidatura", example = "1")
        Long id,

        @Schema(description = "Nome completo do candidato", example = "Maria Silva")
        String nomeCandidato,

        @Schema(description = "Identificador de contato do candidato", example = "12345678900")
        String emailCandidato, // Ou telefone, dependendo do contato

        @Schema(description = "Título da vaga candidatada", example = "Analista de Sistemas Pleno")
        String tituloVaga,

        @Schema(description = "Departamento da vaga", example = "Tecnologia da Informação")
        String departamento,

        @Schema(description = "Data da candidatura", example = "2025-01-10")
        LocalDate dataCandidatura,

        @Schema(description = "Status atual da candidatura", example = "EM_ANALISE")
        StatusCandidatura status) {
    // Construtor auxiliar
    public CandidaturaResponseDTO(Candidatura candidatura) {
        this(
                candidatura.getId(),
                candidatura.getCandidato().getNome() + " " + candidatura.getCandidato().getSobrenome(),
                candidatura.getCandidato().getUsuario().getPessoa().getCpf(),
                candidatura.getVaga().getTitulo(),
                candidatura.getVaga().getDepartamento().getNome(),
                candidatura.getData(),
                candidatura.getStatus());
    }
}
