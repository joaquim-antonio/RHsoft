package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Dados para registro de um novo funcionário")
public record RegisterFuncionarioDTO(

        @Schema(description = "CPF do funcionário (sem pontuação)", example = "12345678900")
        String cpf, // CPF

        @Schema(description = "Senha de acesso", example = "senha123", format = "password")
        String password,

        @Schema(description = "Nome do funcionário", example = "João")
        String nome,

        @Schema(description = "Sobrenome do funcionário", example = "Souza")
        String sobrenome,

        @Schema(description = "Telefone de contato", example = "31988887777")
        String telefone,

        @Schema(description = "Gênero", example = "MASCULINO", allowableValues = {"MASCULINO", "FEMININO", "OUTRO"})
        String sexo,

        @Schema(description = "Data de nascimento", example = "1990-03-15")
        LocalDate dataNascimento,

        @Schema(description = "Endereço do funcionário")
        Endereco endereco,

        @Schema(description = "Salário base mensal", example = "5000.00")
        @Positive(message = "O salário deve ser maior que zero") BigDecimal salario,

        @Schema(description = "Data de admissão", example = "2025-01-01")
        LocalDate dataAdmissao,

        @Schema(description = "Horas trabalhadas por mês", example = "220.0")
        @Positive(message = "Horas trabalhadas devem ser maior que zero") Double horasTrabalhadas,

        @Schema(description = "Cargo do funcionário")
        Cargo cargo,

        @Schema(description = "Vale alimentação mensal", example = "500.00")
        @PositiveOrZero BigDecimal valeAlimentacao,

        @Schema(description = "Conta bancária para pagamento")
        ContaBancaria contaBancaria,

        @Schema(description = "Departamento do funcionário")
        Departamento departamento,

        @Schema(description = "Tipo de acréscimo aplicado ao salário", example = "DIRETO")
        TipoAcrescimo tipoAcrescimo,

        @Schema(description = "Classificação de insalubridade", example = "NENHUM")
        TipoInsalubridade tipoInsalubridade) {
}
