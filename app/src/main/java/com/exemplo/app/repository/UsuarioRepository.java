package com.exemplo.app.repository;

import com.exemplo.app.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

}
