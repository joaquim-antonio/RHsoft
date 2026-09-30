package com.exemplo.app.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.exception.RegraNegocioException; // Importado
import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.model.FaixaIrrf;
import com.exemplo.app.repository.ConfiguracaoRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository repository;

    private static final Long ID_PADRAO = 1L;

    @PostConstruct
    public void inicializarDados() {
        inicializarConfiguracao();
    }

    public void inicializarConfiguracao() {
        if (repository.count() == 0) {
            ConfiguracaoSistema config = new ConfiguracaoSistema();
            
            // --- PARÂMETROS GERAIS ---
            config.setDiaFechamentoMensal(25);
            config.setDiasLimiteReabertura(5);
            config.setSalarioMinimoVigente(new BigDecimal("1518.00"));
            config.setTetoInss(new BigDecimal("8157.41"));
            config.setValorValeAlimentacao(new BigDecimal("600.00"));

            // Percentuais Padrão
            config.setPercentualValeTransporte(new BigDecimal("0.06"));
            config.setPercentualPericulosidade(new BigDecimal("0.30"));
            config.setPercentualInsalubridadeMin(new BigDecimal("0.10"));
            config.setPercentualInsalubridadeMedia(new BigDecimal("0.20"));
            config.setPercentualInsalubridadeMax(new BigDecimal("0.40"));

            // --- INSS ---
            List<FaixaInss> inss = new ArrayList<>();
            inss.add(FaixaInss.builder().ordem(1).aliquota(new BigDecimal("0.075")).limiteInferior(BigDecimal.ZERO).limiteSuperior(new BigDecimal("1518.00")).configuracao(config).build());
            inss.add(FaixaInss.builder().ordem(2).aliquota(new BigDecimal("0.09")).limiteInferior(new BigDecimal("1518.01")).limiteSuperior(new BigDecimal("2793.88")).configuracao(config).build());
            inss.add(FaixaInss.builder().ordem(3).aliquota(new BigDecimal("0.12")).limiteInferior(new BigDecimal("2793.89")).limiteSuperior(new BigDecimal("4190.83")).configuracao(config).build());
            inss.add(FaixaInss.builder().ordem(4).aliquota(new BigDecimal("0.14")).limiteInferior(new BigDecimal("4190.84")).limiteSuperior(new BigDecimal("8157.41")).configuracao(config).build());
            config.setFaixasInss(inss);

            // --- IRRF ---
            config.setIrrfDeducaoPorDependente(new BigDecimal("189.59"));
            
            List<FaixaIrrf> irrf = new ArrayList<>();
            irrf.add(FaixaIrrf.builder().ordem(1).limiteSuperior(new BigDecimal("2259.20")).aliquota(BigDecimal.ZERO).deducao(BigDecimal.ZERO).configuracao(config).build());
            irrf.add(FaixaIrrf.builder().ordem(2).limiteSuperior(new BigDecimal("2826.65")).aliquota(new BigDecimal("0.075")).deducao(new BigDecimal("169.44")).configuracao(config).build());
            irrf.add(FaixaIrrf.builder().ordem(3).limiteSuperior(new BigDecimal("3751.05")).aliquota(new BigDecimal("0.15")).deducao(new BigDecimal("354.80")).configuracao(config).build());
            irrf.add(FaixaIrrf.builder().ordem(4).limiteSuperior(new BigDecimal("4664.68")).aliquota(new BigDecimal("0.225")).deducao(new BigDecimal("636.13")).configuracao(config).build());
            irrf.add(FaixaIrrf.builder().ordem(5).limiteSuperior(null).aliquota(new BigDecimal("0.275")).deducao(new BigDecimal("869.36")).configuracao(config).build());
            
            config.setFaixasIrrf(irrf);

            repository.save(config);
            System.out.println("Configurações do Sistema inicializadas.");
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

        // 1. Atualiza Campos Simples
        atual.setDiaFechamentoMensal(novaConfig.getDiaFechamentoMensal());
        atual.setDiasLimiteReabertura(novaConfig.getDiasLimiteReabertura());
        atual.setSalarioMinimoVigente(novaConfig.getSalarioMinimoVigente());
        atual.setValorValeAlimentacao(novaConfig.getValorValeAlimentacao());
        atual.setTetoInss(novaConfig.getTetoInss());
        
        atual.setPercentualValeTransporte(novaConfig.getPercentualValeTransporte());
        atual.setPercentualPericulosidade(novaConfig.getPercentualPericulosidade());
        atual.setPercentualInsalubridadeMin(novaConfig.getPercentualInsalubridadeMin());
        atual.setPercentualInsalubridadeMedia(novaConfig.getPercentualInsalubridadeMedia());
        atual.setPercentualInsalubridadeMax(novaConfig.getPercentualInsalubridadeMax());
        
        atual.setIrrfDeducaoPorDependente(novaConfig.getIrrfDeducaoPorDependente());

        // 2. Atualiza Lista INSS
        if (novaConfig.getFaixasInss() != null) {
            atual.getFaixasInss().clear();
            for (FaixaInss f : novaConfig.getFaixasInss()) {
                f.setConfiguracao(atual); 
                atual.getFaixasInss().add(f);
            }
        }

        // --- VALIDAÇÃO INSS ---
        validarRegrasInss(atual.getFaixasInss(), atual.getTetoInss());

        // 3. Atualiza Lista IRRF
        if (novaConfig.getFaixasIrrf() != null) {
            atual.getFaixasIrrf().clear();
            for (FaixaIrrf f : novaConfig.getFaixasIrrf()) {
                f.setConfiguracao(atual); 
                atual.getFaixasIrrf().add(f);
            }
        }

        // --- VALIDAÇÃO IRRF ---
        validarRegrasIrrf(atual.getFaixasIrrf());

        return repository.save(atual);
    }

    private void validarRegrasInss(List<FaixaInss> faixas, BigDecimal tetoInss) {
        if (faixas == null || faixas.isEmpty()) return;

        faixas.sort(Comparator.comparingInt(FaixaInss::getOrdem));

        for (int i = 0; i < faixas.size() - 1; i++) {
            FaixaInss atual = faixas.get(i);
            FaixaInss proxima = faixas.get(i + 1);

            // Regra 1: Progressão de Limites
            if (atual.getLimiteSuperior().compareTo(proxima.getLimiteSuperior()) >= 0) {
                throw new RegraNegocioException( // Usando a nova Exceção
                    String.format("Erro na Tabela INSS: O limite da Faixa %d (R$ %.2f) deve ser menor que o da Faixa %d.", 
                    atual.getOrdem(), atual.getLimiteSuperior(), proxima.getOrdem()));
            }

            // Regra 2: Progressão de Alíquotas
            if (atual.getAliquota().compareTo(proxima.getAliquota()) >= 0) {
                throw new RegraNegocioException(
                    String.format("Erro na Tabela INSS: A alíquota da Faixa %d (%.2f%%) não pode ser maior ou igual à da Faixa %d (%.2f%%).", 
                    atual.getOrdem(), atual.getAliquota().multiply(new BigDecimal(100)), 
                    proxima.getOrdem(), proxima.getAliquota().multiply(new BigDecimal(100))));
            }
        }

        // Regra 3: Teto
        FaixaInss ultimaFaixa = faixas.get(faixas.size() - 1);
        if (ultimaFaixa.getLimiteSuperior().compareTo(tetoInss) != 0) {
            throw new RegraNegocioException("Erro de Segurança: O limite da última faixa do INSS deve ser idêntico ao Teto Geral configurado.");
        }
    }

    private void validarRegrasIrrf(List<FaixaIrrf> faixas) {
        if (faixas == null || faixas.isEmpty()) return;

        faixas.sort(Comparator.comparingInt(FaixaIrrf::getOrdem));

        for (int i = 0; i < faixas.size() - 1; i++) {
            FaixaIrrf atual = faixas.get(i);
            FaixaIrrf proxima = faixas.get(i + 1);

            // Regra 1: Progressão de Limites
            if (atual.getLimiteSuperior() != null && proxima.getLimiteSuperior() != null) {
                if (atual.getLimiteSuperior().compareTo(proxima.getLimiteSuperior()) >= 0) {
                    throw new RegraNegocioException(
                        String.format("Erro na Tabela IRRF: O limite da Faixa %d (R$ %.2f) deve ser menor que o da Faixa %d.", 
                        atual.getOrdem(), atual.getLimiteSuperior(), proxima.getOrdem()));
                }
            }

            // Regra 2: Progressão de Alíquotas
            if (atual.getAliquota().compareTo(proxima.getAliquota()) >= 0) {
                throw new RegraNegocioException(
                    String.format("Erro na Tabela IRRF: A alíquota da Faixa %d (%.2f%%) não pode ser maior ou igual à da Faixa %d (%.2f%%).", 
                    atual.getOrdem(), atual.getAliquota().multiply(new BigDecimal(100)), 
                    proxima.getOrdem(), proxima.getAliquota().multiply(new BigDecimal(100))));
            }

            // Regra 3: Progressão de Deduções
            if (atual.getDeducao().compareTo(proxima.getDeducao()) >= 0) {
                throw new RegraNegocioException(
                    String.format("Erro na Tabela IRRF: A dedução da Faixa %d (R$ %.2f) não pode ser maior ou igual à da Faixa %d (R$ %.2f).", 
                    atual.getOrdem(), atual.getDeducao(), 
                    proxima.getOrdem(), proxima.getDeducao()));
            }
        }
    }
}