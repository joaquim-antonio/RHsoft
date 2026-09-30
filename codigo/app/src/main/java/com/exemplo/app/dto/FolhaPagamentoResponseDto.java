package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.FolhaPagamento;

public record FolhaPagamentoResponseDto(
    Long id,
    StatusPagamento status,
    LocalDate dataFechamento,
    LocalDate dataEnvio,        
    BigDecimal totalLiquido,
    BigDecimal horasExtras,      
    BigDecimal adicionalManual,  
    String responsavelNome,
    List<PagamentoResponseDTO> pagamentos 
) {
    public static FolhaPagamentoResponseDto fromEntity(FolhaPagamento folha) {
        List<PagamentoResponseDTO> pagamentosDto = folha.getPagamentos().stream()
            .map(PagamentoResponseDTO::new) 
            .collect(Collectors.toList());

        return new FolhaPagamentoResponseDto(
            folha.getId(),
            folha.getStatus(),
            folha.getDataFechamento(),
            folha.getDataEnvio(),
            folha.getTotalLiquido(),
            folha.getHorasExtras() != null ? folha.getHorasExtras() : BigDecimal.ZERO,
            folha.getAdicionalManual() != null ? folha.getAdicionalManual() : BigDecimal.ZERO,
            folha.getAdministrador() != null ? folha.getAdministrador().getNome() : "Sistema",
            pagamentosDto
        );
    }
}