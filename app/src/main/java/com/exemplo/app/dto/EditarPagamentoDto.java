package com.exemplo.app.dto;
import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;

public record EditarPagamentoDto(
    
    @PositiveOrZero(message = "Horas extras não podem ser negativas")
    BigDecimal horasExtras,

    @PositiveOrZero(message = "O adicional manual não pode ser negativo")
    BigDecimal adicionalManual,

    String codigo) {}
