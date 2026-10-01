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

import com.exemplo.app.model.Departamento;
import com.exemplo.app.service.DepartamentoService;

@WebMvcTest(DepartamentoController.class)
class DepartamentoControllerTest extends BaseControllerTest {

    @MockitoBean
    DepartamentoService departamentoService;

    private String jsonValido() {
        return """
            { "codigo": 1, "nome": "TI", "descricao": "Tecnologia" }
            """;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodos_comAdmin_retorna200() throws Exception {
        when(departamentoService.listarTodosDepartamentos())
                .thenReturn(List.of(new Departamento(1L, "TI", "Tecnologia")));

        mockMvc.perform(get("/api/v1/departamento"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorCodigo_quandoExiste_retorna200() throws Exception {
        when(departamentoService.buscarDepartamentoPorCodigo(1L))
                .thenReturn(new Departamento(1L, "TI", "Tecnologia"));

        mockMvc.perform(get("/api/v1/departamento/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorCodigo_quandoFalha_retorna404() throws Exception {
        when(departamentoService.buscarDepartamentoPorCodigo(1L))
                .thenThrow(new RuntimeException("Departamento nao encontrado"));

        mockMvc.perform(get("/api/v1/departamento/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criar_retorna201() throws Exception {
        when(departamentoService.criarDepartamento(any()))
                .thenReturn(new Departamento(1L, "TI", "Tecnologia"));

        mockMvc.perform(post("/api/v1/departamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criar_comNomeDuplicado_retorna400() throws Exception {
        when(departamentoService.criarDepartamento(any()))
                .thenThrow(new IllegalArgumentException("Nome ja utilizado"));

        mockMvc.perform(post("/api/v1/departamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizar_retorna200() throws Exception {
        when(departamentoService.atualizarDepartamento(anyLong(), any()))
                .thenReturn(new Departamento(1L, "TI", "Tecnologia"));

        mockMvc.perform(put("/api/v1/departamento/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizar_quandoFalha_retorna404() throws Exception {
        when(departamentoService.atualizarDepartamento(anyLong(), any()))
                .thenThrow(new RuntimeException("Departamento nao encontrado"));

        mockMvc.perform(put("/api/v1/departamento/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletar_retorna204() throws Exception {
        doNothing().when(departamentoService).deletarDepartamento(1L);

        mockMvc.perform(delete("/api/v1/departamento/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletar_comFuncionarios_retorna409() throws Exception {
        doThrow(new IllegalStateException("Departamento possui funcionarios"))
                .when(departamentoService).deletarDepartamento(1L);

        mockMvc.perform(delete("/api/v1/departamento/1"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletar_outroErro_retorna404() throws Exception {
        doThrow(new RuntimeException("Departamento nao encontrado"))
                .when(departamentoService).deletarDepartamento(1L);

        mockMvc.perform(delete("/api/v1/departamento/1"))
                .andExpect(status().isNotFound());
    }
}
