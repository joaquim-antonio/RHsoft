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
            p.getMensagens(),
            p.getFuncionario().getNome() + " " + p.getFuncionario().getSobrenome(),
            p.getFuncionario().getCpf(),
            p.getItens()
        );
    }
}