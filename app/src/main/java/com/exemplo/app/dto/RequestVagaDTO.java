package com.exemplo.app.dto;

import java.time.LocalDate;

public record RequestVagaDTO(
            String funcao,
            String titulo,
            String descricao,
            LocalDate dataLimite,
            Long cargoId,
            Long departamentoId
){}
