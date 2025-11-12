package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.exemplo.app.model.ItemPagamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PagamentoRequestDTO(
    @NotBlank
    String codigo,
    @NotBlank
    String cbo,
    @NotNull
    LocalDate vencimento,
    String mensagens,
    @NotBlank
    String mesAnoReferencia,
    @NotBlank
    String funcionarioCpf,
    @NotNull
    Long folhaPagamentoId,
    List<ItemPagamento> itens
) {
}
