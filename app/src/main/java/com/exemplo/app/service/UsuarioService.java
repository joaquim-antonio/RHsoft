package com.exemplo.app.service;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;

// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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


    public Page<Usuario> pesquisarUsuario(String username, String email, Boolean ativo, Pageable pageable){
        Specification<Usuario> spec = Specification.where(null);
        if(username != null && !username.isBlank()) spec = spec.and(likeIgnoreCase("username", username));
        if(email != null && !email.isBlank()) spec = spec.and(likeIgnoreCase("email", email));
        if(ativo != null) spec = spec.and(equalsVal("ativo", ativo));
        return usuarioRepository.findAll(spec, pageable);
    }

    private Specification<Usuario> equalsVal(String string, Boolean ativo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'equalsVal'");
    }

    public Usuario getIdUsuario(Long id){
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario não encontrado"));
    }

    public Usuario criarUsuario(Usuario u, Long funcionarioId){
        if(usuarioRepository.existsByUsername(u.getUsername())){
            throw new BadRequestException("Username já usado");
        }
        if(pessoaRepository.existsByEmail(u.getEmail())){
            throw new BadRequestException("Email já usado");
        }
        u.setPasswordHash(encoder.encode(u.getPasswordHash()));
        if(funcionarioId != null){
            Funcionario f = funcionarioRepository.findById(funcionarioId)
                    .orElseThrow(() -> new NotFoundException("Funcionario não encontrado para vincular"));
            u.setFuncionario(f);
            f.setUsuario(u);
        }
        return usuarioRepository.save(u);
    }

    public Usuario atualizarUsuario(Long id, Usuario u, Long funcionarioId){
        Usuario original = getById(id);
        if(!original.getUsername().equals(u.getUsername()) && usuarioRepository.existsByUsername(u.getUsername())){
            throw new BadRequestException("Username já usado");
        }
        if(!original.getEmail().equals(u.getEmail()) && usuarioRepository.existsByEmail(u.getEmail())){
            throw new BadRequestException("Email já usado");
        }
        original.setUsername(u.getUsername());
        original.setEmail(u.getEmail());
        if(u.getPasswordHash() != null && !u.getPasswordHash().isBlank()){
            original.setPasswordHash(encoder.encode(u.getPasswordHash()));
        }
        if(u.getRole() != null){ original.setRole(u.getRole()); }
        original.setAtivo(u.isAtivo());

        if(funcionarioId != null){
            Funcionario f = funcionarioRepository.findById(funcionarioId)
                    .orElseThrow(() -> new NotFoundException("Funcionario não encontrado para vincular"));
            original.setFuncionario(f);
            f.setUsuario(original);
        }
        return usuarioRepository.save(original);
    }

    public void deletetarUsuario(Long id){
        Usuario u = getById(id);
        usuarioRepository.delete(u);
    }
}
