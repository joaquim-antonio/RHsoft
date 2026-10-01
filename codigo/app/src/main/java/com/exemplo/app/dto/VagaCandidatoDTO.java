package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Vaga;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma vaga exibida ao candidato")
public record VagaCandidatoDTO(

    @Schema(description = "Código da vaga", example = "1")
    Long id,

    @Schema(description = "Título da vaga", example = "Analista de Sistemas Pleno")
    String titulo,

    @Schema(description = "Função exercida", example = "Analista de Sistemas")
    String funcao,

    @Schema(description = "Descrição das responsabilidades", example = "Desenvolver e manter aplicações Java.")
    String descricao,

    @Schema(description = "Departamento da vaga", example = "Tecnologia da Informação")
    String nomeDepartamento,

    @Schema(description = "Data limite para candidatura", example = "2025-12-31")
    LocalDate dataLimite
) {
    public VagaCandidatoDTO(Vaga vaga) {
        this(
            vaga.getId(),
            vaga.getTitulo(),
            vaga.getFuncao(),
            vaga.getDescricao(),
            (vaga.getDepartamento() != null) ? vaga.getDepartamento().getNome() : "Geral",

            vaga.getDataLimite()
        );
    }
}
