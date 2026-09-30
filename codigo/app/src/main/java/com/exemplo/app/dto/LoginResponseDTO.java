package com.exemplo.app.dto;

public record LoginResponseDTO(
    String cpf,
    String nome, 
    String token,
    String role
) {}