package com.exemplo.app.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.exemplo.app.model.Pagamento;

public interface PagamentoRepository extends CrudRepository<Pagamento, String>{

    List<Pagamento> findByFuncionarioCpf(String cpf);
    
}
