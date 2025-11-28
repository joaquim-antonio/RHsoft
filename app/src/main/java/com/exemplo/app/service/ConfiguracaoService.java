package com.exemplo.app.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.repository.ConfiguracaoRepository;
import com.exemplo.app.repository.FaixaInssRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository repository;

    @Autowired
    private FaixaInssRepository faixaInssRepository;

    private static final Long ID_PADRAO = 1L;

    /**
     * Inicializa todos os dados fiscais e de sistema ao subir a aplicação.
     */
    @PostConstruct
    public void inicializarDados() {
        inicializarConfiguracao();
        inicializarFaixasInss();
    }

    public void inicializarConfiguracao() {
        if (repository.count() == 0) {
            ConfiguracaoSistema config = new ConfiguracaoSistema();
            
            // --- PARÂMETROS GERAIS ---
            config.setDiaFechamentoMensal(25);
            config.setDiasLimiteReabertura(5);
            
            // Valores Econômicos 2025
            config.setSalarioMinimoVigente(new BigDecimal("1518.00"));
            config.setTetoInss(new BigDecimal("8157.41"));
            config.setValorValeAlimentacao(new BigDecimal("600.00"));

            // Percentuais Padrão
            config.setPercentualValeTransporte(new BigDecimal("0.06"));
            config.setPercentualPericulosidade(new BigDecimal("0.30"));
            
            config.setPercentualInsalubridadeMin(new BigDecimal("0.10"));
            config.setPercentualInsalubridadeMedia(new BigDecimal("0.20"));
            config.setPercentualInsalubridadeMax(new BigDecimal("0.40"));

            // --- TABELA IRRF 2025---
            config.setIrrfDeducaoPorDependente(new BigDecimal("189.59")); // MUITO CHATO DE IMPLEMENTAR, MAS TÁ AQUI, CASO QUEIRAM NO FUTURO, MAS FUJAM PARAS AS COLINAS IMEDIATAMENTE
            
            // Faixa 1 (Isento)
            config.setIrrfLimiteIsento(new BigDecimal("2259.20"));
            
            // Faixa 2
            config.setIrrfLimiteFaixa2(new BigDecimal("2826.65"));
            config.setIrrfDeducaoFaixa2(new BigDecimal("169.44"));
            
            // Faixa 3
            config.setIrrfLimiteFaixa3(new BigDecimal("3751.05"));
            config.setIrrfDeducaoFaixa3(new BigDecimal("354.80"));
            
            // Faixa 4
            config.setIrrfLimiteFaixa4(new BigDecimal("4664.68"));
            config.setIrrfDeducaoFaixa4(new BigDecimal("636.13"));
            
            // Faixa 5 (Teto)
            config.setIrrfDeducaoFaixa5(new BigDecimal("869.36"));

            repository.save(config);
            System.out.println("Configurações do Sistema e Tabela IRRF inicializadas.");
        }
    }

    private void inicializarFaixasInss() {
        if (faixaInssRepository.count() == 0) {
            // Faixa 1: 7,5% até um salário mínimo
            faixaInssRepository.save(FaixaInss.builder()
                .ordem(1).aliquota(new BigDecimal("0.075"))
                .limiteInferior(BigDecimal.ZERO)
                .limiteSuperior(new BigDecimal("1518.00")).build());

            // Faixa 2: 9%
            faixaInssRepository.save(FaixaInss.builder()
                .ordem(2).aliquota(new BigDecimal("0.09"))
                .limiteInferior(new BigDecimal("1518.01"))
                .limiteSuperior(new BigDecimal("2793.88")).build());

            // Faixa 3: 12%
            faixaInssRepository.save(FaixaInss.builder()
                .ordem(3).aliquota(new BigDecimal("0.12"))
                .limiteInferior(new BigDecimal("2793.89"))
                .limiteSuperior(new BigDecimal("4190.83")).build());

            // Faixa 4: 14% até o teto
            faixaInssRepository.save(FaixaInss.builder()
                .ordem(4).aliquota(new BigDecimal("0.14"))
                .limiteInferior(new BigDecimal("4190.84"))
                .limiteSuperior(new BigDecimal("8157.41")).build());
            
            System.out.println("Tabela Progressiva INSS inicializada.");
        }
    }

    public ConfiguracaoSistema buscarConfiguracaoAtual() {
        return repository.findById(ID_PADRAO)
                .or(() -> repository.findAll().stream().findFirst())
                .orElseThrow(() -> new IllegalStateException("Nenhuma configuração encontrada no sistema."));
    }

    @Transactional
    public ConfiguracaoSistema atualizarConfiguracao(ConfiguracaoSistema novaConfig) {
        ConfiguracaoSistema atual = buscarConfiguracaoAtual();

        atual.setDiaFechamentoMensal(novaConfig.getDiaFechamentoMensal());
        atual.setDiasLimiteReabertura(novaConfig.getDiasLimiteReabertura());
        atual.setSalarioMinimoVigente(novaConfig.getSalarioMinimoVigente());
        atual.setValorValeAlimentacao(novaConfig.getValorValeAlimentacao());
        atual.setTetoInss(novaConfig.getTetoInss());
        
        atual.setPercentualValeTransporte(novaConfig.getPercentualValeTransporte());
        atual.setPercentualPericulosidade(novaConfig.getPercentualPericulosidade());
        
        if (novaConfig.getPercentualInsalubridadeMedia() != null) {
            atual.setPercentualInsalubridadeMin(novaConfig.getPercentualInsalubridadeMin());
            atual.setPercentualInsalubridadeMedia(novaConfig.getPercentualInsalubridadeMedia());
            atual.setPercentualInsalubridadeMax(novaConfig.getPercentualInsalubridadeMax());
        }

        if (novaConfig.getIrrfLimiteIsento() != null) {

            // MUITO CHATO DE IMPLEMENTAR, MAS TÁ AQUI, CASO QUEIRAM NO FUTURO, MAS FUJAM PARAS AS COLINAS IMEDIATAMENTE
            atual.setIrrfDeducaoPorDependente(novaConfig.getIrrfDeducaoPorDependente());
            
            atual.setIrrfLimiteIsento(novaConfig.getIrrfLimiteIsento());
            
            atual.setIrrfLimiteFaixa2(novaConfig.getIrrfLimiteFaixa2());
            atual.setIrrfDeducaoFaixa2(novaConfig.getIrrfDeducaoFaixa2());
            
            atual.setIrrfLimiteFaixa3(novaConfig.getIrrfLimiteFaixa3());
            atual.setIrrfDeducaoFaixa3(novaConfig.getIrrfDeducaoFaixa3());
            
            atual.setIrrfLimiteFaixa4(novaConfig.getIrrfLimiteFaixa4());
            atual.setIrrfDeducaoFaixa4(novaConfig.getIrrfDeducaoFaixa4());
            
            atual.setIrrfDeducaoFaixa5(novaConfig.getIrrfDeducaoFaixa5());
        }

        return repository.save(atual);
    }
}