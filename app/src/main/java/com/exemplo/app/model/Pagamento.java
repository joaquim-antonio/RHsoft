package com.exemplo.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private LocalDate vencimento;

    private String mensagens;

    @NotBlank
    private String mesAnoReferencia;

    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPagamento> itens;
}
