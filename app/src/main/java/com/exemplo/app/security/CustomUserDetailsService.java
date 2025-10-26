package com.exemplo.app.security;

import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementação customizada do UserDetailsService do Spring Security.
 * 
 * Responsável por carregar os dados do usuário do banco de dados
 * e convertê-los para o formato esperado pelo Spring Security.
 * 
 * @author Manus
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carrega os detalhes do usuário pelo username.
     * 
     * Este método é chamado automaticamente pelo Spring Security durante
     * o processo de autenticação.
     *
     * @param username nome de usuário
     * @return UserDetails contendo informações do usuário
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com username: " + username));

        // Verifica se o usuário está ativo
        if (!usuario.isAtivo()) {
            throw new UsernameNotFoundException(
                    "Usuário inativo: " + username);
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(!usuario.isAtivo())
                .credentialsExpired(false)
                .disabled(!usuario.isAtivo())
                .build();
    }

    /**
     * Obtém as autoridades (roles) do usuário.
     * 
     * Por padrão, todos os usuários têm a role USER.
     * Este método pode ser expandido para incluir roles específicas
     * baseadas em atributos do usuário.
     *
     * @param usuario usuário do sistema
     * @return coleção de autoridades
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Role padrão para todos os usuários
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Adiciona role de ADMIN se o usuário for admin
        if ("admin".equalsIgnoreCase(usuario.getUsername())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        
        return authorities;
    }

    /**
     * Carrega o usuário completo do banco de dados.
     * 
     * Útil para operações que precisam de mais informações
     * além das fornecidas pelo UserDetails.
     *
     * @param username nome de usuário
     * @return entidade Usuario completa
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Transactional(readOnly = true)
    public Usuario loadUsuarioByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com username: " + username));
    }
}

