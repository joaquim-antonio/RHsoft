package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Pessoa;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface PessoaRepository extends JpaRepository<Pessoa, String> {
    UserDetails findByCpf(String cpf);
}
