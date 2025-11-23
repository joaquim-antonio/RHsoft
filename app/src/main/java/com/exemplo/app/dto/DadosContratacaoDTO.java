package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;

import jakarta.validation.constraints.NotNull;

public record DadosContratacaoDTO(
        @NotNull BigDecimal salario,
        @NotNull LocalDate dataAdmissao,
        @NotNull Long cargoId,
        @NotNull Long departamentoId,
        @NotNull Double horasTrabalhadas,

        @NotNull String agencia,
        @NotNull String numeroConta,
        @NotNull String nomeBanco,
        String chavePix,

        TipoAcrescimo tipoAcrescimo,
        TipoInsalubridade tipoInsalubridade) {
}