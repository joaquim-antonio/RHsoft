package com.exemplo.app.dto;

import com.exemplo.app.model.Enums.StatusCandidatura;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Novo status a aplicar a uma candidatura")
public record AtualizarStatusDTO(

        @Schema(description = "Status da candidatura", example = "APROVADA", required = true)
        @NotNull(message = "O novo status é obrigatório") StatusCandidatura status) {
}
