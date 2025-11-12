package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Funcionario;

import java.util.Optional;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    boolean existsByChavePix(String chavePix);
    Optional<ContaBancaria> findByFuncionario(Funcionario funcionario);
    
}
