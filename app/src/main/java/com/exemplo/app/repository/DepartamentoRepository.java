package com.exemplo.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.exemplo.app.model.Departamento;

public interface DepartamentoRepository extends CrudRepository<Departamento, String> {

}