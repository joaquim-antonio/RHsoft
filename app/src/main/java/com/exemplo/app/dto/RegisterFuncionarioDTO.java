package com.exemplo.app.dto;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Endereco;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterFuncionarioDTO(
    String cpf, // CPF
    String password,
    String nome,
    String sobrenome,
    String telefone,
    String sexo,
    LocalDate dataNascimento,
    Endereco endereco,
    BigDecimal salario,
    LocalDate dataAdmissao,
    Double horasTrabalhadas,
    Cargo cargo,
    ContaBancaria contaBancaria,
    Departamento departamento
) {}