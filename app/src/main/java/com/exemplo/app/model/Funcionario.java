package com.exemplo.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("FUNCIONARIO")
public class Funcionario extends Pessoa{
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salario;

    @NotNull
    private LocalDate dataAdmissao;

    private Double horasTrabalhadas;

    private Double horasExtras;

    //@NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cargo_codigo", foreignKey=@ForeignKey(name = "fk_funcionario_cargo"))
    private Cargo cargo;

    //@NotNull
    @OneToOne(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private ContaBancaria contaBancaria;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "departamento_id", foreignKey=@ForeignKey(name = "fk_funcionario_departamento"))
    private Departamento departamento;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();
}