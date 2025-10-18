package com.exemplo.app.model;

import java.time.LocalDate;

import com.exemplo.app.model.Enums.StatusPagamento;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@NoArgsConstructor
@AllArgsConstructor
public class FolhaPagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    private double totalLiquido;

    @NotBlank
    private String mes;

    @NotBlank
    private byte ano;

    @NotBlank
    private LocalDate dataFechamento;

    @NotBlank
    private StatusPagamento status;
}
