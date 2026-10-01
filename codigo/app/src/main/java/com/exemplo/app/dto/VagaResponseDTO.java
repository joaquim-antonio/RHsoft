package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Vaga;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados completos de uma vaga")
public record VagaResponseDTO(

        @Schema(description = "Código da vaga", example = "1")
        Long id,

        @Schema(description = "Título da vaga", example = "Analista de Sistemas Pleno")
        String titulo,

        @Schema(description = "Função exercida", example = "Analista de Sistemas")
        String funcao,

        @Schema(description = "Descrição das responsabilidades", example = "Desenvolver e manter aplicações Java.")
        String descricao,

        @Schema(description = "Data limite para candidatura", example = "2025-12-31")
        LocalDate dataLimite,

        @Schema(description = "Código do cargo vinculado", example = "252105")
        Long cargoId,

        @Schema(description = "Nome do cargo vinculado", example = "Analista de Sistemas")
        String nomeCargo,

        @Schema(description = "Código do departamento vinculado", example = "1")
        Long departamentoId,

        @Schema(description = "Nome do departamento vinculado", example = "Tecnologia da Informação")
        String nomeDepartamento,

        @Schema(description = "Indica se a vaga ainda aceita candidaturas", example = "true")
        boolean aberta) {

    public VagaResponseDTO(Vaga vaga) {
        this(
            vaga.getId(),
            vaga.getTitulo(),
            vaga.getFuncao(),
            vaga.getDescricao(),
            vaga.getDataLimite(),

            vaga.getCargo().getCodigo(),
            vaga.getCargo().getNome(),

            vaga.getDepartamento().getCodigo(),
            vaga.getDepartamento().getNome(),

            vaga.getDataLimite().isAfter(LocalDate.now()) || vaga.getDataLimite().isEqual(LocalDate.now())
        );
    }
}
