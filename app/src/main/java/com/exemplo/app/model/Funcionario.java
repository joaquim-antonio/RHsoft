package com.exemplo.app.model;


// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario extends Pessoa {
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salario;

    @NotNull
    private LocalDate dataAdmissao;

    private Double horasTrabalhadas;

    private Double horasExtras;

    @OneToOne(mappedBy = "funcionario", cascade = CascadeType.ALL)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_codigo")
    private Cargo cargo;

    @OneToOne(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "conta_bancaria_id", referencedColumnName = "id")
    private ContaBancaria contaBancaria;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

}
