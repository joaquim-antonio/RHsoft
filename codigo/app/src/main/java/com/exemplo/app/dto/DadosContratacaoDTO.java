package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DadosContratacaoDTO(
        @NotNull @NotNull 
        @Positive(message = "O salário deve ser positivo") BigDecimal salario,
        @NotNull LocalDate dataAdmissao,
        @NotNull Long cargoId,
        @NotNull Long departamentoId,
        @NotNull @Positive(message = "Carga horária deve ser positiva") Double horasTrabalhadas,

        @NotNull String agencia,
        @NotNull String numeroConta,
        @NotNull String nomeBanco,
        String chavePix,

        TipoAcrescimo tipoAcrescimo,
        TipoInsalubridade tipoInsalubridade) {
}