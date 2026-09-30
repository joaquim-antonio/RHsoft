package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record RegisterFuncionarioDTO(
        String cpf, // CPF
        String password,
        String nome,
        String sobrenome,
        String telefone,
        String sexo,
        LocalDate dataNascimento,
        Endereco endereco,
        @Positive(message = "O salário deve ser maior que zero") BigDecimal salario,
        LocalDate dataAdmissao,
        @Positive(message = "Horas trabalhadas devem ser maior que zero") Double horasTrabalhadas,
        Cargo cargo,
        @PositiveOrZero BigDecimal valeAlimentacao,
        ContaBancaria contaBancaria,
        Departamento departamento,
        TipoAcrescimo tipoAcrescimo,
        TipoInsalubridade tipoInsalubridade) {
}