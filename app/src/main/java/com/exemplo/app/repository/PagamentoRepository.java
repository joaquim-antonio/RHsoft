package com.exemplo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.exemplo.app.model.Pagamento;

public interface PagamentoRepository extends CrudRepository<Pagamento, Long>{

    List<Pagamento> findByFuncionarioCpf(String cpf);

    Optional<Pagamento> findByCodigo(String codigo);
    
}
