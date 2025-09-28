package com.exemplo.app.repository;

import com.exemplo.app.model.Pessoa;
import org.springframework.data.repository.CrudRepository;

public interface PessoaRepository extends CrudRepository<Pessoa, String> {

}
