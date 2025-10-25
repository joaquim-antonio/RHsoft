package com.exemplo.app.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Funcionario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface FuncionarioRepository extends CrudRepository<Funcionario, String> {

    UserDetails findByCpf(String cpf);

    boolean existsByCargoCodigo(Long codigo);

    boolean existsByDepartamentoCodigo(Long codigo);
} 