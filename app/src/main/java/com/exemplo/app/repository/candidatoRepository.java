package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Candidato;

public interface CandidatoRepository extends JpaRepository<Candidato, String> {

}