package com.exemplo.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.infra.security.TokenService;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.service.CandidatoService;
import com.exemplo.app.service.FuncionarioService;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

    @MockitoBean
    FuncionarioService funcionarioService;

    @MockitoBean
    CandidatoService candidatoService;

    @MockitoBean
    AuthenticationManager authenticationManager;

    private Funcionario pessoaAutenticada() {
        Funcionario f = new Funcionario();
        f.setCpf("12345678900");
        f.setNome("Maria");
        return f;
    }

    @Test
    void login_comCredenciaisValidas_retorna200ComToken() throws Exception {
        Funcionario pessoa = pessoaAutenticada();
        var auth = new UsernamePasswordAuthenticationToken(
                pessoa, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateToken(pessoa)).thenReturn("token-jwt");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"cpf\": \"12345678900\", \"password\": \"senha123\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    @Test
    void login_semAutoridadeUsaRoleDefault() throws Exception {
        Funcionario pessoa = pessoaAutenticada();
        var auth = new UsernamePasswordAuthenticationToken(pessoa, null, List.of());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenService.generateToken(pessoa)).thenReturn("token-jwt");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"cpf\": \"12345678900\", \"password\": \"senha123\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void login_comCredenciaisInvalidas_retorna401() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("invalido"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"cpf\": \"12345678900\", \"password\": \"errada\" }"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerFuncionario_retorna200() throws Exception {
        mockMvc.perform(post("/auth/register-funcionario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(funcionarioService).register(any());
    }

    @Test
    void registerCandidato_retorna200() throws Exception {
        mockMvc.perform(post("/auth/register-candidato")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(candidatoService).register(any());
    }
}
