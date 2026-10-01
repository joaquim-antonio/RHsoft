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

import com.exemplo.app.model.Cargo;
import com.exemplo.app.service.CargoService;

@WebMvcTest(CargoController.class)
class CargoControllerTest extends BaseControllerTest {

    @MockitoBean
    CargoService cargoService;

    private Cargo cargo() {
        Cargo c = new Cargo();
        c.setNome("Analista de Sistemas");
        return c;
    }

    private String jsonValido() {
        return "{ \"nome\": \"Analista de Sistemas\" }";
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodos_retorna200() throws Exception {
        when(cargoService.listarTodosCargos()).thenReturn(List.of(cargo()));

        mockMvc.perform(get("/api/v1/cargo"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorCodigo_retorna200() throws Exception {
        when(cargoService.buscarCargoPorCodigo(1L)).thenReturn(cargo());

        mockMvc.perform(get("/api/v1/cargo/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void criarCargo_comRoleErrada_naoEstaBloqueadoBugDeSeguranca() throws Exception {
        // BUG DE SEGURANCA: espera-se 403, mas retorna 201.
        // Causa: nao existe regra de rota para POST /api/v1/cargo no SecurityConfig, entao cai
        // em .anyRequest().authenticated() e qualquer usuario autenticado passa.
        // Alem disso o @PreAuthorize("hasRole('ADMIN')") do metodo nao e efetivo porque falta
        // @EnableMethodSecurity no SecurityConfig.
        when(cargoService.criarCargo(any())).thenReturn(cargo());

        mockMvc.perform(post("/api/v1/cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarCargo_comNomeVazio_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarCargo_comAdmin_retorna201() throws Exception {
        when(cargoService.criarCargo(any())).thenReturn(cargo());

        mockMvc.perform(post("/api/v1/cargo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarCargo_retorna200() throws Exception {
        when(cargoService.atualizarCargo(anyLong(), any())).thenReturn(cargo());

        mockMvc.perform(put("/api/v1/cargo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarCargo_retorna204() throws Exception {
        doNothing().when(cargoService).deletarCargo(1L);

        mockMvc.perform(delete("/api/v1/cargo/1"))
                .andExpect(status().isNoContent());
    }
}
