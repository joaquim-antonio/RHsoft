package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;

public record PagamentoResponseDTO(
    String codigo,
    String cbo,
    String mesAnoReferencia,
    LocalDate vencimento,
    BigDecimal proventos,
    BigDecimal descontos,
    BigDecimal valorLiquido,
    BigDecimal salarioBase,      // Adicionado
    BigDecimal horasExtras,      // Adicionado
    BigDecimal valeTransporte,   // Adicionado
    BigDecimal valeAlimentacao,  // Adicionado
    String mensagens,
    String nomeFuncionario, 
    String cpfFuncionario,
    List<ItemPagamento> itens
) {
    public PagamentoResponseDTO(Pagamento p) {
        this(
            p.getCodigo(),
            p.getCbo(),
            p.getMesAnoReferencia(),
            p.getVencimento(),
            p.getProventos(),       
            p.getDescontos(),       
            p.getValorLiquido(),    
            p.getSalarioBase(),     
            p.getHorasExtras() != null ? p.getHorasExtras() : BigDecimal.ZERO, 
            p.getValeTransporte(),
            p.getValeAlimentacao(),
            p.getMensagens(),
            p.getFuncionario() != null ? p.getFuncionario().getNome() + " " + p.getFuncionario().getSobrenome() : "N/A",
            p.getFuncionario() != null ? p.getFuncionario().getCpf() : "N/A",
            p.getItens()
        );
    }
}