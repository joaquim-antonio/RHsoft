package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Vaga;

public record VagaCandidatoDTO(
    Long id,
    String titulo,
    String funcao,
    String descricao,
    String nomeDepartamento, 
    LocalDate dataLimite
) {
    public VagaCandidatoDTO(Vaga vaga) {
        this(
            vaga.getId(),
            vaga.getTitulo(),
            vaga.getFuncao(),
            vaga.getDescricao(),
            (vaga.getDepartamento() != null) ? vaga.getDepartamento().getNome() : "Geral",
            
            vaga.getDataLimite()
        );
    }
}