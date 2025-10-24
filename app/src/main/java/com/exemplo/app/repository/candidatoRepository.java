package com.exemplo.app.repository;
import com.exemplo.app.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CandidatoRepository extends JpaRepository<Candidato, String> {
    
}
