package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, String> {
}
