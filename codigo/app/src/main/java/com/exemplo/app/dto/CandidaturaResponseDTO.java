package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Candidatura;
import com.exemplo.app.model.Enums.StatusCandidatura;

public record CandidaturaResponseDTO(
        Long id,
        String nomeCandidato,
        String emailCandidato, // Ou telefone, dependendo do contato
        String tituloVaga,
        String departamento,
        LocalDate dataCandidatura,
        StatusCandidatura status) {
    // Construtor auxiliar
    public CandidaturaResponseDTO(Candidatura candidatura) {
        this(
                candidatura.getId(),
                candidatura.getCandidato().getNome() + " " + candidatura.getCandidato().getSobrenome(),
                candidatura.getCandidato().getUsuario().getPessoa().getCpf(),
                candidatura.getVaga().getTitulo(),
                candidatura.getVaga().getDepartamento().getNome(),
                candidatura.getData(),
                candidatura.getStatus());
    }
}