package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
    
}
