package com.exemplo.app.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @Column(name = "dias_limite_reabertura")
    private Integer diasLimiteReabertura;
    
    @NotNull
    @Column(name = "salario_minimo", precision = 10, scale = 2)
    private BigDecimal salarioMinimoVigente;

    @NotNull
    @Column(name = "valor_vale_alimentacao", precision = 10, scale = 2)
    private BigDecimal valorValeAlimentacao;

    @Column(name = "teto_inss", precision = 10, scale = 2)
    private BigDecimal tetoInss;

    // IRRF

    @Column(precision = 10, scale = 2)
    private BigDecimal irrfDeducaoPorDependente; // MUITO CHATO DE IMPLEMENTAR, MAS TÁ AQUI, CASO QUEIRAM NO FUTURO, MAS FUJAM PARAS AS COLINAS IMEDIATAMENTE

    // Faixa 1 (Isento)
    @Column(precision = 10, scale = 2)
    private BigDecimal irrfLimiteIsento;

    @Column(precision = 10, scale = 2)
    private BigDecimal irrfLimiteFaixa2;
    @Column(precision = 5, scale = 4)
    private BigDecimal irrfAliquotaFaixa2; 
    @Column(precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa2;

    // Faixa 3
    @Column(precision = 10, scale = 2)
    private BigDecimal irrfLimiteFaixa3;
    @Column(precision = 5, scale = 4) 
    private BigDecimal irrfAliquotaFaixa3;
    @Column(precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa3;

    // Faixa 4
    @Column(precision = 10, scale = 2)
    private BigDecimal irrfLimiteFaixa4;
    @Column(precision = 5, scale = 4) 
    private BigDecimal irrfAliquotaFaixa4;
    @Column(precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa4;

    // Faixa 5 (27,5% ou mais)
    @Column(precision = 5, scale = 4) 
    private BigDecimal irrfAliquotaFaixa5;
    @Column(precision = 10, scale = 2)
    private BigDecimal irrfDeducaoFaixa5;


    // --- PERCENTUAIS ---

    @Column(precision = 5, scale = 4)
    private BigDecimal percentualValeTransporte; // Padrão 0.06

    @Column(precision = 5, scale = 4)
    private BigDecimal percentualInsalubridadeMin; // 0.10

    @Column(precision = 5, scale = 4)
    private BigDecimal percentualInsalubridadeMedia; // 0.20

    @Column(precision = 5, scale = 4)
    private BigDecimal percentualInsalubridadeMax; // 0.40

    @Column(precision = 5, scale = 4)
    private BigDecimal percentualPericulosidade; // 0.30


}