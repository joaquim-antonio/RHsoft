package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.exception.RegraNegocioException;
import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.model.FaixaIrrf;
import com.exemplo.app.repository.ConfiguracaoRepository;

@ExtendWith(MockitoExtension.class)
class ConfiguracaoServiceTest {

    @Mock
    ConfiguracaoRepository repository;

    @InjectMocks
    ConfiguracaoService service;

    private ConfiguracaoSistema configBase() {
        ConfiguracaoSistema c = new ConfiguracaoSistema();
        c.setDiaFechamentoMensal(25);
        c.setDiasLimiteReabertura(5);
        c.setSalarioMinimoVigente(new BigDecimal("1518.00"));
        c.setTetoInss(new BigDecimal("8157.41"));
        c.setValorValeAlimentacao(new BigDecimal("600.00"));
        c.setFaixasInss(new ArrayList<>(List.of(
                faixaInss(1, "0.075", "1518.00"),
                faixaInss(2, "0.09", "2793.88"),
                faixaInss(3, "0.12", "4190.83"),
                faixaInss(4, "0.14", "8157.41"))));
        c.setFaixasIrrf(new ArrayList<>(List.of(
                faixaIrrf(1, "0", "2259.20", "0"),
                faixaIrrf(2, "0.075", "2826.65", "169.44"),
                faixaIrrf(3, "0.15", "3751.05", "354.80"),
                faixaIrrf(4, "0.225", "4664.68", "636.13"),
                faixaIrrf(5, "0.275", null, "869.36"))));
        return c;
    }

    private FaixaInss faixaInss(int ordem, String aliquota, String limiteSuperior) {
        return FaixaInss.builder()
                .ordem(ordem)
                .aliquota(new BigDecimal(aliquota))
                .limiteInferior(BigDecimal.ZERO)
                .limiteSuperior(new BigDecimal(limiteSuperior))
                .build();
    }

    private FaixaIrrf faixaIrrf(int ordem, String aliquota, String limiteSuperior, String deducao) {
        return FaixaIrrf.builder()
                .ordem(ordem)
                .aliquota(new BigDecimal(aliquota))
                .limiteSuperior(limiteSuperior == null ? null : new BigDecimal(limiteSuperior))
                .deducao(new BigDecimal(deducao))
                .build();
    }

    @Test
    void inicializarConfiguracao_quandoVazia_criaPadrao() {
        when(repository.count()).thenReturn(0L);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.inicializarConfiguracao();

        verify(repository).save(any(ConfiguracaoSistema.class));
    }

    @Test
    void inicializarConfiguracao_quandoJaExiste_naoSalva() {
        when(repository.count()).thenReturn(1L);
        service.inicializarConfiguracao();
        verify(repository, never()).save(any());
    }

    @Test
    void buscarConfiguracaoAtual_quandoExisteComIdPadrao_retorna() {
        ConfiguracaoSistema c = configBase();
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        assertSame(c, service.buscarConfiguracaoAtual());
    }

    @Test
    void buscarConfiguracaoAtual_quandoIdPadraoAusente_usaPrimeiroDaLista() {
        ConfiguracaoSistema c = configBase();
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(List.of(c));
        assertSame(c, service.buscarConfiguracaoAtual());
    }

    @Test
    void buscarConfiguracaoAtual_quandoNenhumaExiste_lanca() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(List.of());
        assertThrows(IllegalStateException.class, () -> service.buscarConfiguracaoAtual());
    }

    @Test
    void atualizarConfiguracao_comDadosValidos_atualiza() {
        ConfiguracaoSistema atual = configBase();
        ConfiguracaoSistema nova = configBase();
        nova.setSalarioMinimoVigente(new BigDecimal("1600.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(atual));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ConfiguracaoSistema resultado = service.atualizarConfiguracao(nova);
        assertEquals(0, new BigDecimal("1600.00").compareTo(resultado.getSalarioMinimoVigente()));
    }

    @Test
    void atualizarConfiguracao_comLimiteInssDecrescente_lanca() {
        ConfiguracaoSistema atual = configBase();
        ConfiguracaoSistema nova = configBase();
        // Inverte os limites para quebrar a regra de progressão
        nova.getFaixasInss().get(0).setLimiteSuperior(new BigDecimal("9000.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(atual));
        assertThrows(RegraNegocioException.class, () -> service.atualizarConfiguracao(nova));
    }

    @Test
    void atualizarConfiguracao_comAliquotaInssDecrescente_lanca() {
        ConfiguracaoSistema atual = configBase();
        ConfiguracaoSistema nova = configBase();
        nova.getFaixasInss().get(0).setAliquota(new BigDecimal("0.30"));

        when(repository.findById(1L)).thenReturn(Optional.of(atual));
        assertThrows(RegraNegocioException.class, () -> service.atualizarConfiguracao(nova));
    }

    @Test
    void atualizarConfiguracao_comUltimaFaixaInssDivergenteDoTeto_lanca() {
        ConfiguracaoSistema atual = configBase();
        ConfiguracaoSistema nova = configBase();
        nova.getFaixasInss().get(3).setLimiteSuperior(new BigDecimal("7000.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(atual));
        assertThrows(RegraNegocioException.class, () -> service.atualizarConfiguracao(nova));
    }

    @Test
    void atualizarConfiguracao_comLimiteIrrfDecrescente_lanca() {
        ConfiguracaoSistema atual = configBase();
        ConfiguracaoSistema nova = configBase();
        nova.getFaixasIrrf().get(0).setLimiteSuperior(new BigDecimal("5000.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(atual));
        assertThrows(RegraNegocioException.class, () -> service.atualizarConfiguracao(nova));
    }

    @Test
    void atualizarConfiguracao_comDeducaoIrrfDecrescente_lanca() {
        ConfiguracaoSistema atual = configBase();
        ConfiguracaoSistema nova = configBase();
        nova.getFaixasIrrf().get(0).setDeducao(new BigDecimal("999.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(atual));
        assertThrows(RegraNegocioException.class, () -> service.atualizarConfiguracao(nova));
    }
}
