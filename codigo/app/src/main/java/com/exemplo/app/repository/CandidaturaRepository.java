package com.exemplo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Candidatura;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {


    boolean existsByCandidatoCpfAndVagaId(String candidatoCpf, Long vagaId);

    // Lista candidaturas de um candidato específico 
    List<Candidatura> findByCandidatoCpf(String candidatoCpf);

    // Lista quem se aplicou para uma vaga específica
    List<Candidatura> findByVagaId(Long vagaId);

    Optional<Candidatura> findByVagaIdAndCandidatoCpf(Long vagaId, String candidatoCpf);
}