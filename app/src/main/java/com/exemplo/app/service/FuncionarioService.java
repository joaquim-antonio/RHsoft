package com.exemplo.app.service;

import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//chamada automaticamente pelo spring security ao tentarmos acessar qualquer endpoint
@Service
public class FuncionarioService implements UserDetailsService {

    @Autowired
    FuncionarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        return this.repository.findByCpf(cpf);
    }
}
