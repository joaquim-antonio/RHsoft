package com.exemplo.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.exemplo.app.dto.DepartamentoCountDTO;
import com.exemplo.app.model.Funcionario;

public interface FuncionarioRepository extends CrudRepository<Funcionario, String> {

    UserDetails findByCpf(String cpf);

    boolean existsByCargoCodigo(Long codigo);

    boolean existsByDepartamentoCodigo(Long codigo);

    @Query("SELECT new com.exemplo.app.dto.DepartamentoCountDTO(f.departamento.nome, COUNT(f)) " +
           "FROM Funcionario f " +
           "GROUP BY f.departamento.nome " +
           "ORDER BY COUNT(f) DESC")
    List<DepartamentoCountDTO> findCountByDepartamento();
} 