package com.exemplo.app.model;


// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    public Funcionario(String nome, double salario, String cargo) {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 120)
    private String nome;

    @NotBlank
    private Cargo cargo;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salario;

    @NotNull
    private LocalDate dataAdmissao;

    private Double horasTrabalhadas;

    private Double horasExtras;

    @OneToOne(mappedBy = "funcionario", cascade = CascadeType.ALL)
    private Usuario usuario;
}
