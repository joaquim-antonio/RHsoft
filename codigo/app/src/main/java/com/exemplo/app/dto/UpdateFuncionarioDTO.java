package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.dto.FuncionarioResponseDTO.BancoData;
import com.exemplo.app.dto.FuncionarioResponseDTO.EnderecoData;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para atualização de um funcionário")
public record UpdateFuncionarioDTO(
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

    // Dados Contratuais
    @Schema(description = "Salário base mensal", example = "5000.00")
    BigDecimal salario,

    @Schema(description = "Data de admissão", example = "2025-01-01")
    LocalDate dataAdmissao,

    @Schema(description = "Carga horária mensal", example = "220.0")
    Double horasTrabalhadas,

    @Schema(description = "Código do cargo", example = "252105")
    Long cargoId,

    @Schema(description = "Código do departamento", example = "1")
    Long departamentoId,

    @Schema(description = "Tipo de acréscimo aplicado ao salário", example = "DIRETO")
    String tipoAcrescimo,

    @Schema(description = "Classificação de insalubridade", example = "NENHUM")
    String tipoInsalubridade,

    @Schema(description = "Endereço do funcionário")
    EnderecoData endereco,

    @Schema(description = "Conta bancária do funcionário")
    BancoData contaBancaria
) {}
