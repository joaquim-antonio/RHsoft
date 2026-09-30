package com.exemplo.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.exemplo.app.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

}
