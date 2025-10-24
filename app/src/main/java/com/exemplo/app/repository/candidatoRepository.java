package com.exemplo.app.repository;
import java.util.Optional;

import com.exemplo.app.model.Candidato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, String> {
    Optional<Candidato> findByAll();

    Optional<Candidato> findByid(Long id);

    
    










    
}
