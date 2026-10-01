package com.exemplo.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.service.CargoService;

/**
 * Testes de regressao do GlobalExceptionHandler.
 *
 * Bug corrigido nesta etapa: requisicoes para rotas inexistentes caíam no handler
 * generico @ExceptionHandler(Exception.class) e devolviam 500 em vez de 404,
 * mascarando erro de cliente como erro de servidor.
 */
@WebMvcTest(CargoController.class)
class GlobalExceptionHandlerTest extends BaseControllerTest {

    /** So e necessario para o slice do controller carregar; nenhuma interacao esperada. */
    @MockitoBean
    CargoService cargoService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void rotaInexistente_retorna404_eNao500() throws Exception {
        mockMvc.perform(get("/api/v1/rota-que-nao-existe/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rotaInexistente_incluiPathNaMensagem() throws Exception {
        mockMvc.perform(get("/api/v1/rota-que-nao-existe/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/api/v1/rota-que-nao-existe/123"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("/api/v1/rota-que-nao-existe/123")));
    }

    @Test
    void rotaInexistente_semAutenticacao_bloqueadoPeloSecurityFilter() throws Exception {
        // Sem token, .anyRequest().authenticated() barra a requisicao ANTES do
        // DispatcherServlet resolver a rota, entao o handler de 404 nem e acionado
        // e a resposta e 403 (e nao 401, por falta de AuthenticationEntryPoint).
        mockMvc.perform(get("/api/v1/rota-que-nao-existe/123"))
                .andExpect(status().isForbidden());
    }
}
