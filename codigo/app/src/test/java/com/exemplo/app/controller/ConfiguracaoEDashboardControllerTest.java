package com.exemplo.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.dto.DepartamentoCountDTO;
import com.exemplo.app.dto.DashboardDistribuicaoDTO;
import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.model.FaixaIrrf;
import com.exemplo.app.service.ConfiguracaoService;
import com.exemplo.app.service.DashboardService;

@WebMvcTest({ConfiguracaoController.class, DashboardController.class})
class ConfiguracaoEDashboardControllerTest extends BaseControllerTest {

    @MockitoBean
    ConfiguracaoService configuracaoService;

    @MockitoBean
    DashboardService dashboardService;

    private ConfiguracaoSistema config() {
        ConfiguracaoSistema c = new ConfiguracaoSistema();
        c.setDiaFechamentoMensal(25);
        c.setDiasLimiteReabertura(5);
        c.setSalarioMinimoVigente(new BigDecimal("1518.00"));
        c.setTetoInss(new BigDecimal("8157.41"));
        c.setFaixasInss(new ArrayList<>());
        c.setFaixasIrrf(new ArrayList<>());
        return c;
    }

    private String jsonConfiguracaoValida() {
        return """
            {
              "diaFechamentoMensal": 25,
              "diasLimiteReabertura": 5,
              "salarioMinimoVigente": 1518.00,
              "valorValeAlimentacao": 600.00,
              "tetoInss": 8157.41,
              "percentualValeTransporte": 0.06,
              "percentualInsalubridadeMin": 0.10,
              "percentualInsalubridadeMedia": 0.20,
              "percentualInsalubridadeMax": 0.40,
              "percentualPericulosidade": 0.30
            }
            """;
    }

    // ---------- ConfiguracaoController ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarConfiguracao_comAdmin_retorna200() throws Exception {
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(config());

        mockMvc.perform(get("/api/v1/configuracoes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void buscarConfiguracao_comUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/configuracoes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarConfiguracao_retorna200() throws Exception {
        when(configuracaoService.atualizarConfiguracao(any())).thenReturn(config());

        mockMvc.perform(put("/api/v1/configuracoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConfiguracaoValida()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarConfiguracao_camposObrigatoriosVazios_retorna400() throws Exception {
        mockMvc.perform(put("/api/v1/configuracoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarConfiguracao_regraInvalida_retorna400() throws Exception {
        when(configuracaoService.atualizarConfiguracao(any()))
                .thenThrow(new com.exemplo.app.exception.RegraNegocioException("faixas invalidas"));

        mockMvc.perform(put("/api/v1/configuracoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConfiguracaoValida()))
                .andExpect(status().isBadRequest());
    }

    // ---------- DashboardController ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDistribuicao_retorna200() throws Exception {
        DashboardDistribuicaoDTO dto = new DashboardDistribuicaoDTO(
                10L, List.of(new DepartamentoCountDTO("TI", 4L)));
        when(dashboardService.getDistribuicaoDepartamentos()).thenReturn(dto);

        mockMvc.perform(get("/api/v1/dashboard/distribuicao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFuncionarios").value(10));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDistribuicao_comUser_retorna200() throws Exception {
        when(dashboardService.getDistribuicaoDepartamentos())
                .thenReturn(new DashboardDistribuicaoDTO(0L, List.of()));

        mockMvc.perform(get("/api/v1/dashboard/distribuicao"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CANDIDATO")
    void getDistribuicao_comCandidato_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/distribuicao"))
                .andExpect(status().isForbidden());
    }
}
