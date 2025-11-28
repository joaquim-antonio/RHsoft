package com.exemplo.app.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "faixas_inss")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaixaInss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquota; 

    @Column(name = "limite_inferior", nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteInferior;

    @Column(name="limite_superior" , nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteSuperior;
}