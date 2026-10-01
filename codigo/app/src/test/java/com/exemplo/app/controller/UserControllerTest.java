package com.exemplo.app.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.exemplo.app.model.Funcionario;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    private Funcionario pessoaLogada() {
        Funcionario f = new Funcionario();
        f.setCpf("12345678900");
        f.setNome("Joao");
        f.setSobrenome("Souza");
        return f;
    }

    @Test
    void getMyUser_autenticado_retorna200() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                pessoaLogada(), null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(get("/user/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void getMyUser_semAutoridade_retornaRoleNulo() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(pessoaLogada(), null, List.of());

        mockMvc.perform(get("/user/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void getMyUser_semAutenticacao_retorna403() throws Exception {
        // A rota /user/me exige .authenticated(), mas sem AuthenticationEntryPoint
        // configurado o Spring Security responde 403 em vez de 401.
        mockMvc.perform(get("/user/me"))
                .andExpect(status().isForbidden());
    }
}
