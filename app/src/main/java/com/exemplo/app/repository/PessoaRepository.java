package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.model.Pessoa;


public interface PessoaRepository extends JpaRepository<Pessoa, String> {
    UserDetails findByCpf(String cpf);

    public boolean existsByCpf(String cpf);

    @Modifying
    @Transactional
    @Query(value = "UPDATE pessoa SET dtype = 'FUNCIONARIO' WHERE cpf = :cpf", nativeQuery = true)
    void promoverCandidatoParaFuncionario(String cpf);
}
