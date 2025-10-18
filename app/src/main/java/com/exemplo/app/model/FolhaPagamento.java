package com.exemplo.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.exemplo.app.model.Enums.StatusPagamento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Folha_pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolhaPagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Column(precision = 19, scale = 2)
    private BigDecimal totalLiquido;

    @NotNull
    private LocalDate dataFechamento;

    @NotNull
    private StatusPagamento status;

    @OneToMany(mappedBy = "folhaPagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos;

    @NotNull
    @OneToOne
    @JoinColumn(name = "adminitrador_id")
    private Administrador administrador;

}
