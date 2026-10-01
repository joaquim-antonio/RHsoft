package com.exemplo.app.dto;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

import com.exemplo.app.model.ItemPagamento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para criação de um pagamento")
public record PagamentoRequestDTO(

    @Schema(description = "Código identificador do pagamento", example = "PAG-2025-01-001", required = true)
    @NotBlank
    String codigo,

    @Schema(description = "Código CBO do cargo", example = "252105", required = true)
    @NotBlank
    String cbo,

    @Schema(description = "Data de vencimento do pagamento", example = "2025-02-05", required = true)
    @NotNull
    LocalDate vencimento,

    @Schema(description = "Mensagens ou observações do pagamento", example = "Pagamento referente a janeiro/2025")
    String mensagens,

    @Schema(description = "Vale transporte do período", example = "220.00")
    BigDecimal valeTransporte,

    @Schema(description = "Vale alimentação do período", example = "500.00")
    BigDecimal valeAlimentacao,

    @Schema(description = "Mês/ano de referência do pagamento", example = "01/2025", required = true)
    @NotBlank
    String mesAnoReferencia,

    @Schema(description = "CPF do funcionário que receberá o pagamento", example = "12345678900", required = true)
    @NotBlank
    String funcionarioCpf,

    @Schema(description = "ID da folha de pagamento a vincular", example = "1", required = true)
    @NotNull
    Long folhaPagamentoId,

    @Schema(description = "Itens que compõem proventos e descontos")
    List<ItemPagamento> itens
 ){
}
