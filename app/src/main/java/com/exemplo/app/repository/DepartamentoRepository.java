package com.exemplo.app.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    Optional<Departamento> findByNome(String nome);

    boolean existsByCodigo(Long codigo);

}