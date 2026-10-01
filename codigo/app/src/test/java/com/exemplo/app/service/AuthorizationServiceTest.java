package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.exemplo.app.model.Pessoa;
import com.exemplo.app.repository.PessoaRepository;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    PessoaRepository pessoaRepository;

    @InjectMocks
    AuthorizationService service;

    @Test
    void loadUser_quandoNaoExiste_lancaUsernameNotFound() {
        when(pessoaRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("123"));
    }

    @Test
    void loadUser_quandoExiste_retorna() {
        Pessoa p = new Pessoa();
        when(pessoaRepository.findById("123")).thenReturn(Optional.of(p));
        assertSame(p, service.loadUserByUsername("123"));
    }
}
