package com.exemplo.app.controller;

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

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.service.FolhaPagamentoService;

@WebMvcTest(FolhaPagamentoController.class)
class FolhaPagamentoControllerTest extends BaseControllerTest {

    @MockitoBean
    FolhaPagamentoService folhaService;

    private FolhaPagamento folha(StatusPagamento status) {
        FolhaPagamento f = new FolhaPagamento();
        f.setStatus(status);
        f.setPagamentos(new java.util.ArrayList<>());
        return f;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_retorna200() throws Exception {
        when(folhaService.listarTodasDTO()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/folha-pagamento/listar"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void abrirFolha_retorna200() throws Exception {
        when(folhaService.abrirFolha(anyString())).thenReturn(folha(StatusPagamento.ABERTO));

        mockMvc.perform(post("/api/v1/folha-pagamento/abrir"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void abrirFolha_quandoJaExiste_lancaE_vira500() throws Exception {
        when(folhaService.abrirFolha(anyString())).thenThrow(new RuntimeException("Ja existe folha aberta"));

        mockMvc.perform(post("/api/v1/folha-pagamento/abrir"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarFolha_retorna200() throws Exception {
        when(folhaService.buscarFolhaPorId(1L)).thenReturn(folha(StatusPagamento.ABERTO));

        mockMvc.perform(get("/api/v1/folha-pagamento/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarFolha_inexistente_vira500() throws Exception {
        when(folhaService.buscarFolhaPorId(1L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Folha nao encontrada"));

        mockMvc.perform(get("/api/v1/folha-pagamento/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editarPagamento_retorna200() throws Exception {
        Pagamento p = new Pagamento();
        p.setItens(new java.util.ArrayList<>());
        p.setFuncionario(new com.exemplo.app.model.Funcionario());
        when(folhaService.editarPagamento(any())).thenReturn(p);

        mockMvc.perform(put("/api/v1/folha-pagamento/pagamento/editar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"PAG-1\",\"quantidadeHorasExtras\":10,\"adicionalManual\":50}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fecharFolha_retorna200() throws Exception {
        when(folhaService.fecharFolha(1L)).thenReturn(folha(StatusPagamento.FECHADA));

        mockMvc.perform(post("/api/v1/folha-pagamento/fechar/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void consolidarFolha_retorna200() throws Exception {
        when(folhaService.consolidarFolha(1L)).thenReturn(folha(StatusPagamento.CONSOLIDADA));

        mockMvc.perform(post("/api/v1/folha-pagamento/consolidar/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reabrirFolha_retorna200() throws Exception {
        when(folhaService.reabrirFolha(1L)).thenReturn(folha(StatusPagamento.ABERTO));

        mockMvc.perform(post("/api/v1/folha-pagamento/reabrir/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void gerarFolha_retorna200() throws Exception {
        doNothing().when(folhaService).gerarFolhaDePagamento(1L);

        mockMvc.perform(post("/api/v1/folha-pagamento/gerar/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void enviarFolha_retorna200() throws Exception {
        doNothing().when(folhaService).enviarFolhaParaFuncionarios(1L);

        mockMvc.perform(post("/api/v1/folha-pagamento/1/enviar"))
                .andExpect(status().isOk());
    }

    @Test
    void listar_semAutenticacao_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/folha-pagamento/listar"))
                .andExpect(status().isForbidden());
    }
}
