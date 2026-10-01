package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.FolhaPagamento;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma folha de pagamento")
public record FolhaPagamentoResponseDto(
    @Schema(description = "Código da folha", example = "1")
    Long id,

    @Schema(description = "Status atual da folha", example = "ABERTA", allowableValues = {"ABERTA", "FECHADA", "CONSOLIDADA", "ENVIADA"})
    StatusPagamento status,

    @Schema(description = "Data de fechamento da folha", example = "2025-02-05")
    LocalDate dataFechamento,

    @Schema(description = "Data de envio aos funcionários", example = "2025-02-06")
    LocalDate dataEnvio,

    @Schema(description = "Total líquido da folha", example = "53000.00")
    BigDecimal totalLiquido,

    @Schema(description = "Total de horas extras da folha", example = "3000.00")
    BigDecimal horasExtras,

    @Schema(description = "Adicional manual aplicado à folha", example = "0.00")
    BigDecimal adicionalManual,

    @Schema(description = "Nome do administrador responsável pela folha", example = "João Souza")
    String responsavelNome,

    @Schema(description = "Pagamentos que compõem a folha")
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
