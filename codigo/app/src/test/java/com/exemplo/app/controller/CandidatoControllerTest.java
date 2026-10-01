package com.exemplo.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.service.CandidatoService;

@WebMvcTest(CandidatoController.class)
class CandidatoControllerTest extends BaseControllerTest {

    @MockitoBean
    CandidatoService candidatoService;

    private Candidato candidato() {
        Candidato c = new Candidato();
        c.setCpf("12345678900");
        c.setNome("Maria");
        return c;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarCandidatos_retorna200() throws Exception {
        when(candidatoService.listarTodosOsCandidatos()).thenReturn(List.of(candidato()));

        mockMvc.perform(get("/api/v1/Candidato/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void buscarPerfil_retorna200() throws Exception {
        when(candidatoService.buscarPerfilPorCpf(anyString()))
                .thenReturn(new com.exemplo.app.dto.CandidatoProfileDTO(
                        "12345678900", "Maria", "Silva", "31988887777",
                        List.of("Java"), List.of(), List.of()));

        mockMvc.perform(get("/api/v1/Candidato/12345678900"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void buscarPerfil_quandoFalha_retorna404() throws Exception {
        when(candidatoService.buscarPerfilPorCpf(anyString()))
                .thenThrow(new RuntimeException("Candidato nao encontrado"));

        mockMvc.perform(get("/api/v1/Candidato/12345678900"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void atualizarPerfil_retorna200() throws Exception {
        when(candidatoService.atualizarPerfil(anyString(), any())).thenReturn(candidato());

        mockMvc.perform(put("/api/v1/Candidato/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void atualizarPerfil_quandoFalha_retorna404() throws Exception {
        when(candidatoService.atualizarPerfil(anyString(), any()))
                .thenThrow(new RuntimeException("Candidato nao encontrado"));

        mockMvc.perform(put("/api/v1/Candidato/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }
}
