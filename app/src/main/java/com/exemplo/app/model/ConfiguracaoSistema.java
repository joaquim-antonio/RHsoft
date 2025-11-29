package com.exemplo.app.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuracoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- PRAZOS ---

    @NotNull(message = "O dia de fechamento é obrigatório")
    @Min(value = 1, message = "O dia deve ser maior que 0")
    @Max(value = 28, message = "Para segurança, o dia de fechamento deve ser até 28")
    @Column(name = "dia_fechamento_mensal")
    private Integer diaFechamentoMensal;

    @NotNull(message = "O prazo de reabertura é obrigatório")
    @Min(value = 0, message = "O prazo não pode ser negativo")
    @PositiveOrZero(message = "O prazo não pode ser negativo")
    @Column(name = "dias_limite_reabertura")
    private Integer diasLimiteReabertura;
    
    @NotNull
    @Positive(message = "O salário mínimo deve ser maior que zero")
    @Column(name = "salario_minimo", precision = 10, scale = 2)
    private BigDecimal salarioMinimoVigente;

    @NotNull
    @PositiveOrZero(message = "O valor do VA não pode ser negativo")
    @Column(name = "valor_vale_alimentacao", precision = 10, scale = 2)
    private BigDecimal valorValeAlimentacao;

    @NotNull
    @Column(name = "teto_inss", precision = 10, scale = 2)
    @Positive(message = "O teto do INSS deve ser positivo")
    private BigDecimal tetoInss;

    // IRRF
    @Column(precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal irrfDeducaoPorDependente; // MUITO CHATO DE IMPLEMENTAR, MAS TÁ AQUI, CASO QUEIRAM NO FUTURO, MAS FUJAM PARAS AS COLINAS IMEDIATAMENTE

    // Faixa 1 (Isento)
    @NotNull
    @Column(name = "irrf_limite_isento", precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal irrfLimiteIsento;

    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_limite_faixa_2", precision = 10, scale = 2)
    private BigDecimal irrfLimiteFaixa2;
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_aliquota_faixa_2", precision = 5, scale = 4)
    private BigDecimal irrfAliquotaFaixa2; 
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_deducao_faixa_2" , precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa2;

    // Faixa 3
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_limite_faixa_3", precision = 10, scale = 2)
    private BigDecimal irrfLimiteFaixa3;
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_aliquota_faixa_3", precision = 5, scale = 4) 
    private BigDecimal irrfAliquotaFaixa3;
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_deducao_faixa_3", precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa3;

    // Faixa 4
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_limite_faixa_4", precision = 10, scale = 2)
    private BigDecimal irrfLimiteFaixa4;
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_limite_aliquota_4", precision = 5, scale = 4) 
    private BigDecimal irrfAliquotaFaixa4;
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_deducao_faixa_4", precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa4;

    // Faixa 5 (27,5% ou mais)
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_aliquota_faixa_5", precision = 5, scale = 4) 
    private BigDecimal irrfAliquotaFaixa5;
    @NotNull
    @PositiveOrZero
    @Column(name = "irrf_deducao_faixa_5", precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa5;


    // --- PERCENTUAIS ---

    @NotNull
    @PositiveOrZero
    @Column(name = "percentual_vale_transporte", precision = 5, scale = 4)
    private BigDecimal percentualValeTransporte; // Padrão 0.06

    @NotNull
    @PositiveOrZero
    @Column(name = "percentual_insalubridade_min", precision = 5, scale = 4)
    private BigDecimal percentualInsalubridadeMin; // 0.10

    @NotNull
    @PositiveOrZero
    @Column(name = "percentual_insalubridade_media", precision = 5, scale = 4)
    private BigDecimal percentualInsalubridadeMedia; // 0.20

    @NotNull
    @PositiveOrZero
    @Column(name = "percentual_insalubridade_max", precision = 5, scale = 4)
    private BigDecimal percentualInsalubridadeMax; // 0.40

    @NotNull
    @PositiveOrZero
    @Column(name = "percentual_periculosidade", precision = 5, scale = 4)
    private BigDecimal percentualPericulosidade; // 0.30


}