package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Endereco;

// DTO para o registro de um novo candidato.
public record RegisterCandidatoDTO(
    String cpf,
    String password,
    String nome,
    String sobrenome,
    String telefone,
    String sexo,
    LocalDate dataNascimento,
    Endereco endereco
) {}