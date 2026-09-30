package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.dto.FuncionarioResponseDTO.BancoData;
import com.exemplo.app.dto.FuncionarioResponseDTO.EnderecoData;

public record UpdateFuncionarioDTO(
    String nome,
    String sobrenome,
    String telefone,
    String sexo, 
    LocalDate dataNascimento,
    
    // Dados Contratuais
    BigDecimal salario,
    LocalDate dataAdmissao,
    Double horasTrabalhadas,
    
    Long cargoId,
    Long departamentoId,

    String tipoAcrescimo, 
    String tipoInsalubridade,
    EnderecoData endereco,
    BancoData contaBancaria
) {}