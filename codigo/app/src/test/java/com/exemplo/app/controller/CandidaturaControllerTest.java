package com.exemplo.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Candidatura;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Enums.StatusCandidatura;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.service.CandidaturaService;

@WebMvcTest(CandidaturaController.class)
class CandidaturaControllerTest extends BaseControllerTest {

    @MockitoBean
    CandidaturaService candidaturaService;

    private Candidatura candidatura() {
        // CandidaturaResponseDTO navega por candidato -> usuario -> pessoa, entao
        // o grafo inteiro precisa estar montado para o DTO nao estourar NPE.
        Candidato candidato = new Candidato();
        candidato.setCpf("12345678900");
        candidato.setNome("Maria");
        candidato.setSobrenome("Silva");
        Usuario usuario = new Usuario();
        usuario.setPessoa(candidato);
        candidato.setUsuario(usuario);

        Cargo cargo = new Cargo();
        cargo.setNome("Analista");

        Vaga vaga = new Vaga();
        vaga.setTitulo("Analista Pleno");
        vaga.setDepartamento(new Departamento(1L, "TI", "Tecnologia"));
        vaga.setCargo(cargo);

        Candidatura c = new Candidatura();
        c.setCandidato(candidato);
        c.setVaga(vaga);
        c.setData(LocalDate.now());
        c.setStatus(StatusCandidatura.ABERTA);
        return c;
    }

    /**
     * O controller le o CPF via Authentication.getName(). Com @WithMockUser o nome padrao
     * seria "user", entao forcamos um principal com o CPF real e a role necessaria
     * para passar pelas regras de rota do SecurityConfig.
     */
    private RequestPostProcessor comoCpf(String cpf) {
        return authentication(new UsernamePasswordAuthenticationToken(
                cpf, null, List.of(new SimpleGrantedAuthority("ROLE_CANDIDATO"))));
    }

    /** Todos os campos de DadosContratacaoDTO sao @NotNull, exceto os 3 ultimos. */
    private String jsonContratacao() {
        return """
            {
              "salario": 5000.00,
              "dataAdmissao": "2025-02-01",
              "cargoId": 1,
              "departamentoId": 1,
              "horasTrabalhadas": 220.0,
              "agencia": "1234",
              "numeroConta": "56789-0",
              "nomeBanco": "Banco do Brasil",
              "chavePix": "maria@email.com"
            }
            """;
    }

    @Test
    void aplicarParaVaga_comCandidato_retorna201() throws Exception {
        when(candidaturaService.aplicarParaVaga(anyString(), anyLong())).thenReturn(candidatura());

        mockMvc.perform(post("/api/v1/candidaturas/aplicar/1").with(comoCpf("12345678900")))
                .andExpect(status().isCreated());
    }

    @Test
    void aplicarParaVaga_quandoDuplicada_retorna400() throws Exception {
        when(candidaturaService.aplicarParaVaga(anyString(), anyLong()))
                .thenThrow(new IllegalStateException("Candidato ja candidatou-se a esta vaga"));

        mockMvc.perform(post("/api/v1/candidaturas/aplicar/1").with(comoCpf("12345678900")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aplicarParaVaga_quandoErroInesperado_retorna500() throws Exception {
        when(candidaturaService.aplicarParaVaga(anyString(), anyLong()))
                .thenThrow(new RuntimeException("falha no banco"));

        mockMvc.perform(post("/api/v1/candidaturas/aplicar/1").with(comoCpf("12345678900")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void cancelarCandidatura_retorna200() throws Exception {
        doNothing().when(candidaturaService).cancelarCandidatura(anyLong(), anyString());

        mockMvc.perform(delete("/api/v1/candidaturas/cancelar/1").with(comoCpf("12345678900")))
                .andExpect(status().isOk());
    }

    @Test
    void cancelarCandidatura_quandoFalha_retorna400() throws Exception {
        doThrow(new RuntimeException("Candidatura nao encontrada"))
                .when(candidaturaService).cancelarCandidatura(anyLong(), anyString());

        mockMvc.perform(delete("/api/v1/candidaturas/cancelar/1").with(comoCpf("12345678900")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void minhasCandidaturas_retorna200() throws Exception {
        when(candidaturaService.listarMinhasCandidaturas(anyString()))
                .thenReturn(List.of(candidatura()));

        mockMvc.perform(get("/api/v1/candidaturas/minhas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verCandidatosPorVaga_retorna200() throws Exception {
        when(candidaturaService.listarCandidatosDaVaga(1L))
                .thenReturn(List.of(candidatura()));

        mockMvc.perform(get("/api/v1/candidaturas/vaga/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarStatus_retorna200() throws Exception {
        when(candidaturaService.atualizarStatus(anyLong(), any()))
                .thenReturn(candidatura());

        mockMvc.perform(patch("/api/v1/candidaturas/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"APROVADA\" }"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarStatus_transicaoInvalida_retorna400() throws Exception {
        when(candidaturaService.atualizarStatus(anyLong(), any()))
                .thenThrow(new IllegalStateException("Transicao invalida"));

        mockMvc.perform(patch("/api/v1/candidaturas/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"APROVADA\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void aprovarEContratar_retorna200() throws Exception {
        when(candidaturaService.aprovarEContratar(anyLong(), any())).thenReturn(candidatura());

        mockMvc.perform(post("/api/v1/candidaturas/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContratacao()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void aprovarEContratar_dadosIncompletos_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/candidaturas/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void aprovarEContratar_regraDeNegocio_retorna400() throws Exception {
        when(candidaturaService.aprovarEContratar(anyLong(), any()))
                .thenThrow(new IllegalStateException("Candidatura nao esta em status aprovavel"));

        mockMvc.perform(post("/api/v1/candidaturas/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContratacao()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void aprovarEContratar_erroInesperado_retorna500() throws Exception {
        when(candidaturaService.aprovarEContratar(anyLong(), any()))
                .thenThrow(new RuntimeException("falha ao criar funcionario"));

        mockMvc.perform(post("/api/v1/candidaturas/1/aprovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContratacao()))
                .andExpect(status().isInternalServerError());
    }
}
