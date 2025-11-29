package com.exemplo.app.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, String> {

    Optional<Administrador> findByCpf(String cpf);

}
