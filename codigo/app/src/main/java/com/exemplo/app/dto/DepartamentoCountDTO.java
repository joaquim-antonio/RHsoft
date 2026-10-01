package com.exemplo.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Quantidade de funcionários em um departamento")
public record DepartamentoCountDTO(
        @Schema(description = "Nome do departamento", example = "Tecnologia da Informação")
        String nomeDepartamento,

        @Schema(description = "Quantidade de funcionários no departamento", example = "15")
        long count) {
    public DepartamentoCountDTO(String nomeDepartamento, long count) {
        this.nomeDepartamento = nomeDepartamento != null ? nomeDepartamento : "Sem Depto.";
        this.count = count;
    }
}
