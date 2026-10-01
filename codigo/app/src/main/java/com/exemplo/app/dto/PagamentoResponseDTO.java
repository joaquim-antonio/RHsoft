package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um pagamento de funcionário")
public record PagamentoResponseDTO(

    @Schema(description = "Código identificador do pagamento", example = "PAG-2025-01-001")
    String codigo,

    @Schema(description = "Código CBO do cargo", example = "252105")
    String cbo,

    @Schema(description = "Mês/ano de referência", example = "01/2025")
    String mesAnoReferencia,

    @Schema(description = "Data de vencimento", example = "2025-02-05")
    LocalDate vencimento,

    @Schema(description = "Total de proventos", example = "6500.00")
    BigDecimal proventos,

    @Schema(description = "Total de descontos", example = "1200.00")
    BigDecimal descontos,

    @Schema(description = "Valor líquido a receber", example = "5300.00")
    BigDecimal valorLiquido,

    @Schema(description = "Salário base", example = "5000.00") // Adicionado
    BigDecimal salarioBase,

    @Schema(description = "Total de horas extras", example = "300.00") // Adicionado
    BigDecimal horasExtras,

    @Schema(description = "Vale transporte", example = "220.00") // Adicionado
    BigDecimal valeTransporte,

    @Schema(description = "Vale alimentação", example = "500.00") // Adicionado
    BigDecimal valeAlimentacao,

    @Schema(description = "Mensagens ou observações do pagamento", example = "Pagamento referente a janeiro/2025")
    String mensagens,

    @Schema(description = "Nome completo do funcionário", example = "João Souza")
    String nomeFuncionario,

    @Schema(description = "CPF do funcionário", example = "12345678900")
    String cpfFuncionario,

    @Schema(description = "Itens que compõem proventos e descontos")
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
