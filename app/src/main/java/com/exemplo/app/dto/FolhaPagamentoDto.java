package com.exemplo.app.dto;


import java.math.BigDecimal;

public record FolhaPagamentoDto(

        // Identificação do pagamento / funcionário
        String codigoPagamento,
        String nomeFuncionario,
        String cpfFuncionario,
        String cargoFuncionario,

        // Entradas editáveis
        BigDecimal horasExtras,
        BigDecimal adicionalManual,
        BigDecimal valeAlimentacao,
        BigDecimal valeTransporte,

       
        BigDecimal salarioBase,
        BigDecimal inss,
        BigDecimal fgts,
        BigDecimal totalDescontos,
        BigDecimal totalProventos,
        BigDecimal liquidoFinal

) {}
