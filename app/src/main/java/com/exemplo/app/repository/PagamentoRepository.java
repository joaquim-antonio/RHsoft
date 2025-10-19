package com.exemplo.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.exemplo.app.model.Pagamento;

public interface PagamentoRepository extends CrudRepository<Pagamento, String>{
    
}
