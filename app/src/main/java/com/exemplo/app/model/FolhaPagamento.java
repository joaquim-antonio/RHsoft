package com.exemplo.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.exemplo.app.model.Enums.StatusPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Column(precision = 19, scale = 2)
    private BigDecimal totalLiquido; 

    private LocalDate dataFechamento;  

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @JsonIgnore
    @OneToMany(mappedBy = "folhaPagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal horasExtras;   

    @Column(precision = 10, scale = 2)
    private BigDecimal adicionalManual;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "administrador_id", foreignKey=@ForeignKey(name = "fk_folhapagamento_administrador"))
    private Administrador administrador;

    private LocalDate dataEnvio;

}
