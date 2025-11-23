package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.exemplo.app.model.Pessoa;


public interface PessoaRepository extends JpaRepository<Pessoa, String> {
    UserDetails findByCpf(String cpf);
}
