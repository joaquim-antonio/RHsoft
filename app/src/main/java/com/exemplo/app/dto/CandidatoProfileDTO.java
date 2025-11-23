package com.exemplo.app.dto;

import java.util.List;

public record CandidatoProfileDTO(
    String cpf,
    String nome,
    String sobrenome,
    String telefone,
    // String email, Placeholder
    List<String> habilidades,
    List<String> formacao,
    List<String> experiencias
) {}