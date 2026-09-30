package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.FolhaPagamento;

public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamento, Long>{

    public boolean existsByStatus(StatusPagamento statusPagamento);


    
}
