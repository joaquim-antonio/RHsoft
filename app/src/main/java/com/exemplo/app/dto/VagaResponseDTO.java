package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Vaga;

public record VagaResponseDTO(
        Long id,
        String titulo,
        String funcao,
        String descricao,
        LocalDate dataLimite,
    
        Long cargoId,           
        String nomeCargo,
        
        Long departamentoId,    
        String nomeDepartamento,
        
        boolean aberta) {
            
    public VagaResponseDTO(Vaga vaga) {
        this(
            vaga.getId(),
            vaga.getTitulo(),
            vaga.getFuncao(),
            vaga.getDescricao(),
            vaga.getDataLimite(),
            
            vaga.getCargo().getCodigo(),
            vaga.getCargo().getNome(),
            
            vaga.getDepartamento().getCodigo(),
            vaga.getDepartamento().getNome(),
            
            vaga.getDataLimite().isAfter(LocalDate.now()) || vaga.getDataLimite().isEqual(LocalDate.now())
        );
    }
}