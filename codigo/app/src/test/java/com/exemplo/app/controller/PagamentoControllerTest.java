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

import com.exemplo.app.model.Pagamento;
import com.exemplo.app.service.PagamentoService;

@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest extends BaseControllerTest {

    @MockitoBean
    PagamentoService pagamentoService;

    private Pagamento pagamento() {
        Pagamento p = new Pagamento();
        p.setCodigo("PAG-2025-01-001");
        p.setItens(new java.util.ArrayList<>());
        p.setFuncionario(new com.exemplo.app.model.Funcionario());
        return p;
    }

    private String jsonValido() {
        return """
            {
              "codigo": "PAG-2025-01-001",
              "cbo": "252105",
              "vencimento": "2025-02-05",
              "mesAnoReferencia": "01/2025",
              "funcionarioCpf": "12345678900",
              "folhaPagamentoId": 1
            }
            """;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarPagamento_retorna201() throws Exception {
        when(pagamentoService.criarPagamento(any(), anyString(), anyLong())).thenReturn(pagamento());

        mockMvc.perform(post("/pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarPagamento_semCamposObrigatorios_retorna400() throws Exception {
        mockMvc.perform(post("/pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorCodigo_retorna200() throws Exception {
        when(pagamentoService.buscarPagamentoPorCodigo("PAG-1")).thenReturn(pagamento());

        mockMvc.perform(get("/pagamento/PAG-1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarPorFuncionario_retorna200() throws Exception {
        when(pagamentoService.listarPagamentosPorFuncionario("12345678900"))
                .thenReturn(List.of(pagamento()));

        mockMvc.perform(get("/pagamento/funcionario").param("cpf", "12345678900"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void recalcularTotais_retorna200() throws Exception {
        when(pagamentoService.recalcularTotais("PAG-1")).thenReturn(pagamento());

        mockMvc.perform(patch("/pagamento/PAG-1/recalcular"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarPagamento_retorna204() throws Exception {
        doNothing().when(pagamentoService).deletarPagamento("PAG-1");

        mockMvc.perform(delete("/pagamento/PAG-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorCodigo_semAutenticacao_retorna403() throws Exception {
        mockMvc.perform(get("/pagamento/PAG-1"))
                .andExpect(status().isForbidden());
    }
}
