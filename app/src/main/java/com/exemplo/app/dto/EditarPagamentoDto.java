package com.exemplo.app.dto;
import java.math.BigDecimal;

public record EditarPagamentoDto(

    BigDecimal horasExtras,

    BigDecimal adicionalManual,

    BigDecimal adicionalPagamento,
    
    String pagamentoId)
{}