package com.exemplo.app.dto;

public record UserResponseDTO(
    String cpf,
    String nome,
    String sobrenome,
    String role 
) {}