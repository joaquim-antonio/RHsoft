package com.exemplo.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.service.ContaBancariaService;

@WebMvcTest(ContaBancariaController.class)
class ContaBancariaControllerTest extends BaseControllerTest {

    @MockitoBean
    ContaBancariaService contaBancariaService;

    private ContaBancaria conta() {
        ContaBancaria c = new ContaBancaria("1234", "56789-0", "Banco do Brasil", "maria@email.com");
        return c;
    }

    private String jsonValido() {
        return """
            {
              "agencia": "1234",
              "numero": "56789-0",
              "nomeBanco": "Banco do Brasil",
              "chavePix": "maria@email.com",
              "funcionarioCpf": "12345678900"
            }
            """;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarContaBancaria_retorna201() throws Exception {
        when(contaBancariaService.criarContaBancaria(any(), anyString())).thenReturn(conta());

        mockMvc.perform(post("/conta-bancaria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarContaBancaria_semAgencia_retorna400() throws Exception {
        mockMvc.perform(post("/conta-bancaria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_retorna200() throws Exception {
        when(contaBancariaService.buscarContaBancariaPorId(1L)).thenReturn(conta());

        mockMvc.perform(get("/conta-bancaria/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorFuncionario_retorna200() throws Exception {
        when(contaBancariaService.buscarContaBancariaPorFuncionario("12345678900")).thenReturn(conta());

        mockMvc.perform(get("/conta-bancaria/funcionario/12345678900"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarContaBancaria_retorna200() throws Exception {
        when(contaBancariaService.atualizarContaBancaria(
                anyLong(), anyString(), anyString(), anyString(), any(), anyString()))
                .thenReturn(conta());

        mockMvc.perform(put("/conta-bancaria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarChavePix_retorna200() throws Exception {
        when(contaBancariaService.atualizarChavePix(1L, "nova@email.com")).thenReturn(conta());

        mockMvc.perform(patch("/conta-bancaria/1/chave-pix")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("nova@email.com"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarContaBancaria_retorna204() throws Exception {
        doNothing().when(contaBancariaService).deletarContaBancaria(1L);

        mockMvc.perform(delete("/conta-bancaria/1"))
                .andExpect(status().isNoContent());
    }
}
