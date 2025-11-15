package com.exemplo.app.dto;

public record LoginResponseDTO(
        String cpf,
        String token,
        String role) {

}