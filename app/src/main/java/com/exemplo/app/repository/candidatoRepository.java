package com.exemplo.app.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Candidato;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, String> {
    
    Optional<Candidato> findByAll();

    Optional<Candidato> findByid(Long id);

    
    










    
}
