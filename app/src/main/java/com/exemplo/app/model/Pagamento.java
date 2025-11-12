package com.exemplo.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.exemplo.app.model.Enums.TipoItemPagamento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
    @Column(name = "codigo", unique = true, nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private String codigo;

    @NotBlank
    private String cbo;

    @Column(name = "proventos", nullable = false, precision = 19, scale = 2)
    private BigDecimal proventos;

    @Column(name = "descontos", nullable = false, precision = 19, scale = 2)
    private BigDecimal descontos;

    @Column(name = "valor_liquido", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorLiquido;

    @NotNull
    private LocalDate vencimento;

    private String mensagens;

    @NotBlank
    private String mesAnoReferencia;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPagamento> itens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folha_pagamento_id")
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
}
