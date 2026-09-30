package com.exemplo.app.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
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

    @Column(nullable = false, precision = 5, scale = 4)
    @PositiveOrZero(message = "Aliquota não pode ser negativo")
    private BigDecimal aliquota; 

    @Column(name = "limite_inferior", nullable = false, precision = 10, scale = 2)
    @PositiveOrZero(message = "Limite inferior deve ser positivo ou zero")
    private BigDecimal limiteInferior;

    @Column(name="limite_superior" , nullable = false, precision = 10, scale = 2)
    @PositiveOrZero(message="Limite superior deve ser maior que 0")
    private BigDecimal limiteSuperior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracao_id", foreignKey=@ForeignKey(name= "fk_inss_config"))
    @JsonBackReference
    private ConfiguracaoSistema configuracao;
}