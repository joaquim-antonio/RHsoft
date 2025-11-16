package com.exemplo.app.dto;

import java.util.List;

public record DashboardDistribuicaoDTO(
    long totalFuncionarios,
    List<DepartamentoCountDTO> segmentos 
) {}