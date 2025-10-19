package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Enums.StatusPagamento;

public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamento, Long>{


    
}
