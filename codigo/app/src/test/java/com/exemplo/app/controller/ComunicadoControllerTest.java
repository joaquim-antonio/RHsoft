package com.exemplo.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.model.Comunicado;
import com.exemplo.app.service.ComunicadoService;

@WebMvcTest(ComunicadoController.class)
class ComunicadoControllerTest extends BaseControllerTest {

    @MockitoBean
    ComunicadoService comunicadoService;

    private Comunicado comunicado() {
        Comunicado c = new Comunicado();
        c.setTitulo("Ferias");
        c.setConteudo("Aviso de ferias");
        c.setDataPublicacao(LocalDate.now());
        return c;
    }

    @Test
    void listarRecentes_publico_retorna200() throws Exception {
        when(comunicadoService.listarRecentes()).thenReturn(List.of(comunicado()));

        mockMvc.perform(get("/api/v1/comunicados/recentes"))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodos_publico_retorna200() throws Exception {
        Page<Comunicado> page = new PageImpl<>(List.of(comunicado()), PageRequest.of(0, 6), 1);
        when(comunicadoService.listarPaginado(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/comunicados"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_retorna200() throws Exception {
        when(comunicadoService.obterComunicadoPorId(1L)).thenReturn(comunicado());

        mockMvc.perform(get("/api/v1/comunicados/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void criarComunicado_comRoleErrada_retorna403() throws Exception {
        mockMvc.perform(post("/api/v1/comunicados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarComunicado_comAdmin_retorna200() throws Exception {
        when(comunicadoService.criarComunicado(any())).thenReturn(comunicado());

        mockMvc.perform(post("/api/v1/comunicados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarComunicado_retorna200() throws Exception {
        when(comunicadoService.atualizarComunicado(anyLong(), any())).thenReturn(comunicado());

        mockMvc.perform(put("/api/v1/comunicados/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarComunicado_retorna204() throws Exception {
        doNothing().when(comunicadoService).deletarComunicado(1L);

        mockMvc.perform(delete("/api/v1/comunicados/1"))
                .andExpect(status().isNoContent());
    }
}
