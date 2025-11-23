package com.exemplo.app.dto;

import com.exemplo.app.model.Enums.StatusCandidatura;

import jakarta.validation.constraints.NotNull;

public record AtualizarStatusDTO(
        @NotNull(message = "O novo status é obrigatório") StatusCandidatura status) {
}