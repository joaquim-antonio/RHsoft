package com.exemplo.app.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {

    boolean existsByCargoCodigo(String codigo);

    boolean existsByDepartamentoCodigo(String codigo);
    
} 