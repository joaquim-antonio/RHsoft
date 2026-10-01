package com.exemplo.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.service.VagaService;

@WebMvcTest(VagaController.class)
class VagaControllerTest extends BaseControllerTest {

    @MockitoBean
    VagaService vagaService;

    private Vaga vagaValida() {
        Cargo cargo = new Cargo();
        cargo.setNome("Desenvolvedor");
        Departamento depto = new Departamento(1L, "TI", "Tecnologia");
        Vaga v = new Vaga();
        v.setTitulo("Analista Pleno");
        v.setFuncao("Analista");
        v.setDescricao("Descricao");
        v.setDataLimite(LocalDate.now().plusDays(10));
        v.setCargo(cargo);
        v.setDepartamento(depto);
        return v;
    }

    private String jsonValido() {
        return """
            {
              "funcao": "Analista",
              "titulo": "Analista Pleno",
              "descricao": "Descricao",
              "dataLimite": "%s",
              "cargoId": 1,
              "departamentoId": 1
            }
            """.formatted(LocalDate.now().plusDays(10));
    }

    @Test
    void listarDisponiveis_semAutenticacao_retorna200() throws Exception {
        when(vagaService.listarVagasDisponiveis()).thenReturn(List.of(vagaValida()));

        mockMvc.perform(get("/api/v1/vagas/disponiveis"))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodas_semAutenticacao_retorna403() throws Exception {
        // Comportamento atual: sem AuthenticationEntryPoint configurado no SecurityConfig,
        // o Spring Security responde 403 (AccessDeniedHandlerImpl) em vez de 401,
        // embora o Swagger documente 401. Divergencia conhecida.
        mockMvc.perform(get("/api/v1/vagas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodas_comAdmin_retorna200() throws Exception {
        when(vagaService.listarTodasVagas()).thenReturn(List.of(vagaValida()));

        mockMvc.perform(get("/api/v1/vagas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void criarVaga_comRoleErrada_retorna403() throws Exception {
        mockMvc.perform(post("/api/v1/vagas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarVaga_comAdmin_retorna201() throws Exception {
        when(vagaService.criarVaga(any())).thenReturn(vagaValida());

        mockMvc.perform(post("/api/v1/vagas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isCreated());
    }

    @Test
    void buscarVagaPorId_publica_retorna200() throws Exception {
        when(vagaService.buscarVagaPorId(1L)).thenReturn(vagaValida());

        mockMvc.perform(get("/api/v1/vagas/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editarVaga_comAdmin_retorna200() throws Exception {
        when(vagaService.atualizarVaga(anyLong(), any())).thenReturn(vagaValida());

        mockMvc.perform(put("/api/v1/vagas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarVaga_semConflito_retorna204() throws Exception {
        doNothing().when(vagaService).excluirVaga(1L);

        mockMvc.perform(delete("/api/v1/vagas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarVaga_comCandidatos_retorna409() throws Exception {
        doThrow(new IllegalStateException("Vaga possui candidatos"))
                .when(vagaService).excluirVaga(1L);

        mockMvc.perform(delete("/api/v1/vagas/1"))
                .andExpect(status().isConflict());
    }
}
