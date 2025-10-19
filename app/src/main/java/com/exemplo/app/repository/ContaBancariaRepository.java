package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.ContaBancaria;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    boolean existsByChavePix(String chavePix);
    
}
