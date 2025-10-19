package com.exemplo.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.app.model.Cargo;

public interface CargoRepository extends JpaRepository<Cargo, String>{

    Optional<Cargo> findByNome(String nome);
 
    boolean existsByNome(String nome);

} 
