package com.exemplo.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, updatable = false)
    private String codigo;

    @NotBlank
    private String cbo;

    @Column(name = "proventos", nullable = false, precision = 19, scale = 2)
    private BigDecimal proventos;

    @Column(name = "descontos", nullable = false, precision = 19, scale = 2)
    private BigDecimal descontos;

    @Column(name = "valor_liquido", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorLiquido;

    @Column(name = "HorasExtras", nullable = true, precision = 19, scale = 2)
    private BigDecimal horasExtras;

    @Column(name = "SalarioBase", nullable = false, precision = 19, scale = 2)
    private BigDecimal salarioBase;

    @NotNull
    private LocalDate vencimento;

    private String mensagens;

    @Column(name = "ValeTransporte", nullable = false, precision = 19, scale = 2)
    private BigDecimal valeTransporte;

    @Column(name = "ValeAlimentacao", nullable = false, precision = 19, scale = 2)
    private BigDecimal valeAlimentacao;

    @NotBlank
    private String mesAnoReferencia;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", foreignKey = @ForeignKey(name = "fk_pagamento_funcionario"))
    private Funcionario funcionario;

    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPagamento> itens;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folha_pagamento_id", foreignKey = @ForeignKey(name = "fk_pagamento_folhapagamento"))
    private FolhaPagamento folhaPagamento;

    public void calcularTotais() {

        if (this.itens == null || this.itens.isEmpty()) {
            this.proventos = BigDecimal.ZERO;
            this.descontos = BigDecimal.ZERO;
            this.valorLiquido = BigDecimal.ZERO;
            return;
        }

        this.proventos = this.itens.stream()
                .filter(item -> item.getTipo() == TipoItemPagamento.PROVENTO)
                .map(ItemPagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.descontos = this.itens.stream()
                .filter(item -> item.getTipo() == TipoItemPagamento.DESCONTO)
                .map(ItemPagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorLiquido = this.proventos.subtract(this.descontos);
    }

    // Construtor auxiliar para o DTO
    public Pagamento(String codigo, String cbo, BigDecimal proventos, BigDecimal descontos, BigDecimal valorLiquido, LocalDate vencimento, String mensagens, BigDecimal valeTransporte, BigDecimal valeAlimentacao, String mesAnoReferencia, Funcionario funcionario, List<ItemPagamento> itens, FolhaPagamento folhaPagamento) {
        this.codigo = codigo;
        this.cbo = cbo;
        this.proventos = proventos;
        this.descontos = descontos;
        this.valorLiquido = valorLiquido;
        this.vencimento = vencimento;
        this.mensagens = mensagens;
        this.valeTransporte = valeTransporte;
        this.valeAlimentacao = valeAlimentacao;
        this.mesAnoReferencia = mesAnoReferencia;
        this.funcionario = funcionario;
        this.itens = itens;
        this.folhaPagamento = folhaPagamento;
    }

}
