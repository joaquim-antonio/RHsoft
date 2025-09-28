package com.exemplo.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.app.model.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

}
