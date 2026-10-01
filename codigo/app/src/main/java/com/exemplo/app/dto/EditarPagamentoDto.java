package com.exemplo.app.dto;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Dados para edição de um pagamento dentro da folha")
public record EditarPagamentoDto(

    @Schema(description = "Quantidade de horas extras a adicionar", example = "10.0")
    @PositiveOrZero(message = "Horas extras não podem ser negativas")
    Double quantidadeHorasExtras,

    @Schema(description = "Valor adicional manual", example = "150.00")
    @PositiveOrZero(message = "O adicional manual não pode ser negativo")
    BigDecimal adicionalManual,

    @Schema(description = "Código do pagamento a editar", example = "PAG-2025-01-001", required = true)
    String codigo) {}
