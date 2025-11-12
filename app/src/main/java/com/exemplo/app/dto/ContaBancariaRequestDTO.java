package com.exemplo.app.dto;

import jakarta.validation.constraints.NotBlank;

public record ContaBancariaRequestDTO(
    @NotBlank
    String agencia,
    @NotBlank
    String numero,
    @NotBlank
    String nomeBanco,
    String chavePix,
    @NotBlank
    String funcionarioCpf
) {
}
