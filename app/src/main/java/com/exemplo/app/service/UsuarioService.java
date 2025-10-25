package com.exemplo.app.service;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;

// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PessoaRepository;
import com.exemplo.app.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private final FuncionarioRepository funcionarioRepository;

    @Autowired
    private final PessoaRepository pessoaRepository;
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

}
