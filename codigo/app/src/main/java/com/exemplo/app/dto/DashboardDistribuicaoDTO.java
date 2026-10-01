package com.exemplo.app.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Distribuição de funcionários por departamento")
public record DashboardDistribuicaoDTO(
    @Schema(description = "Total de funcionários no órgão", example = "120")
    long totalFuncionarios,

    @Schema(description = "Quantidade de funcionários por departamento")
    List<DepartamentoCountDTO> segmentos
) {}
