package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoGenero;

// DTO para o registro de um novo candidato.
public record RegisterCandidatoDTO(
    String cpf,
    String password,
    String nome,
    String sobrenome,
    String telefone,
    TipoGenero sexo,
    LocalDate dataNascimento,
    Endereco endereco
) {}