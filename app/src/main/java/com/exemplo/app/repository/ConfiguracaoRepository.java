package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.ConfiguracaoSistema;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<ConfiguracaoSistema, Long> {

    
}