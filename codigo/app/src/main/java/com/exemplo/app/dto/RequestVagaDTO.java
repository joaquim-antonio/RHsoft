package com.exemplo.app.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação ou atualização de uma vaga")
public record RequestVagaDTO(

            @Schema(description = "Função exercida na vaga", example = "Analista de Sistemas")
            String funcao,

            @Schema(description = "Título da vaga", example = "Analista de Sistemas Pleno")
            String titulo,

            @Schema(description = "Descrição das responsabilidades e requisitos", example = "Desenvolver e manter aplicações Java.")
            String descricao,

            @Schema(description = "Data limite para candidatura", example = "2025-12-31")
            LocalDate dataLimite,

            @Schema(description = "Código do cargo vinculado à vaga", example = "252105")
            Long cargoId,

            @Schema(description = "Código do departamento vinculado à vaga", example = "1")
            Long departamentoId
){}
