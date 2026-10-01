package com.exemplo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.exemplo.app.infra.security.SecurityConfig;
import com.exemplo.app.infra.security.TokenService;
import com.exemplo.app.repository.PessoaRepository;

/**
 * Base para os testes de slice (@WebMvcTest) dos controllers.
 *
 * O SecurityConfig precisa ser importado explicitamente: o slice web nao inclui
 * @Configuration classes de seguranca, e sem ele o Spring Security usa a
 * auto-configuracao padrao (form login), o que mascara as regras reais de rota.
 *
 * TokenService e PessoaRepository sao mocks porque o SecurityFilter (injetado no
 * SecurityConfig) depende deles, mas nao fazem parte do slice web.
 */
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected TokenService tokenService;

    @MockitoBean
    protected PessoaRepository pessoaRepository;
}
