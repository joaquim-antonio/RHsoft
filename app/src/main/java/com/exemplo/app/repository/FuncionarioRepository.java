package com.exemplo.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.exemplo.app.model.Funcionario;

public interface FuncionarioRepository extends CrudRepository<Funcionario, String> {

    UserDetails findByCpf(String cpf);

    boolean existsByCargoCodigo(Long codigo);

    boolean existsByDepartamentoCodigo(Long codigo);
} 