package com.exemplo.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;
import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.FaixaInssRepository;

@Service
public class CalculadoraFolhaService {

    @Autowired 
    private FaixaInssRepository faixaInssRepository;

    /**
     * Processa o cálculo completo da folha de um funcionário.
     */
    public void processarFolhaFuncionario(Pagamento pagamento, Funcionario func, ConfiguracaoSistema config) {
        List<ItemPagamento> itens = new ArrayList<>();
        BigDecimal salarioBase = func.getSalario();
        BigDecimal totalProventos = BigDecimal.ZERO;
        
        // --- 1. PROVENTOS ---

        // Salário Base
        itens.add(criarItem("Salário Base", TipoItemPagamento.PROVENTO, salarioBase, pagamento));
        totalProventos = totalProventos.add(salarioBase);
        pagamento.setSalarioBase(salarioBase); 

        // Adicionais (Insalubridade / Periculosidade)
        BigDecimal valorAdicional = calcularAdicionais(func, config, salarioBase);
        if (valorAdicional.compareTo(BigDecimal.ZERO) > 0) {
            String nomeAdicional = (func.getTipoAcrescimo() == TipoAcrescimo.INSALUBRIDADE) ? "Insalubridade" : "Periculosidade";
            itens.add(criarItem(nomeAdicional, TipoItemPagamento.PROVENTO, valorAdicional, pagamento));
            totalProventos = totalProventos.add(valorAdicional);
        }

        // Horas Extras
        if(pagamento.getHorasExtras() == null) {
            pagamento.setHorasExtras(BigDecimal.ZERO); 
        }

        // DESCONTOS 

        // INSS Progressivo
        BigDecimal inss = calcularInssProgressivo(totalProventos);
        itens.add(criarItem("INSS", TipoItemPagamento.DESCONTO, inss, pagamento));

        // IRRF (Sem dedução de dependentes)
        BigDecimal baseIRRF = totalProventos.subtract(inss);
        
        // Chamada simplificada sem o parâmetro de dependentes
        BigDecimal irrf = calcularIRRF(baseIRRF, config);
        
        if (irrf.compareTo(BigDecimal.ZERO) > 0) {
            itens.add(criarItem("IRRF", TipoItemPagamento.DESCONTO, irrf, pagamento));
        }

        // Vale Transporte
        BigDecimal vt = salarioBase.multiply(config.getPercentualValeTransporte());
        itens.add(criarItem("Vale Transporte", TipoItemPagamento.DESCONTO, vt, pagamento));
        pagamento.setValeTransporte(vt); 

        // Vale Alimentação
        pagamento.setValeAlimentacao(config.getValorValeAlimentacao());

        
        String nomeCargo = (func.getCargo() != null) ? func.getCargo().getNome() : "Não Informado";
        pagamento.setCbo(nomeCargo);
        pagamento.setMensagens("Cálculo realizado em " + LocalDate.now());

        pagamento.setItens(itens);
        pagamento.calcularTotais(); 
    }

    private BigDecimal calcularAdicionais(Funcionario func, ConfiguracaoSistema config, BigDecimal salarioBase) {
        if (func.getTipoAcrescimo() == null || func.getTipoAcrescimo() == TipoAcrescimo.NENHUM) {
            return BigDecimal.ZERO;
        }

        if (func.getTipoAcrescimo() == TipoAcrescimo.INSALUBRIDADE) {
            BigDecimal baseInsalubridade = config.getSalarioMinimoVigente();
            BigDecimal percentual = config.getPercentualInsalubridadeMedia(); 

            if (func.getTipoInsalubridade() == TipoInsalubridade.BAIXO) {
                percentual = config.getPercentualInsalubridadeMin();
            } else if (func.getTipoInsalubridade() == TipoInsalubridade.ALTO) {
                percentual = config.getPercentualInsalubridadeMax();
            }
            return baseInsalubridade.multiply(percentual);
        } 
        
        if (func.getTipoAcrescimo() == TipoAcrescimo.PERICULOSIDADE) {
            return salarioBase.multiply(config.getPercentualPericulosidade());
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calcularInssProgressivo(BigDecimal salarioBruto) {
        List<FaixaInss> faixas = faixaInssRepository.findAllByOrderByOrdemAsc();
        if(faixas.isEmpty()) return BigDecimal.ZERO;

        BigDecimal impostoTotal = BigDecimal.ZERO;
        BigDecimal tetoINSS = faixas.get(faixas.size() - 1).getLimiteSuperior();
        BigDecimal salarioCalculo = salarioBruto.compareTo(tetoINSS) > 0 ? tetoINSS : salarioBruto;

        for (FaixaInss faixa : faixas) {
            if (salarioCalculo.compareTo(faixa.getLimiteInferior()) <= 0) break;

            BigDecimal tetoFaixa = salarioCalculo.compareTo(faixa.getLimiteSuperior()) > 0 
                ? faixa.getLimiteSuperior() : salarioCalculo;
            
            BigDecimal baseFaixa = tetoFaixa.subtract(faixa.getLimiteInferior());
            
            if (baseFaixa.compareTo(BigDecimal.ZERO) > 0) {
                impostoTotal = impostoTotal.add(baseFaixa.multiply(faixa.getAliquota()));
            }
        }
        return impostoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula IRRF.
     * Apenas: (Base * Alíquota) - Dedução da Faixa
     */
    private BigDecimal calcularIRRF(BigDecimal base, ConfiguracaoSistema config) {
        BigDecimal baseCalculo = base;

        if (baseCalculo.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        
        // Faixa 1: Isento
        if (baseCalculo.compareTo(config.getIrrfLimiteIsento()) <= 0) {
            return BigDecimal.ZERO;
        }

        // Faixa 2: 7.5%
        if (baseCalculo.compareTo(config.getIrrfLimiteFaixa2()) <= 0) {
            return baseCalculo.multiply(new BigDecimal("0.075"))
                    .subtract(config.getIrrfDeducaoFaixa2())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Faixa 3: 15%
        if (baseCalculo.compareTo(config.getIrrfLimiteFaixa3()) <= 0) {
            return baseCalculo.multiply(new BigDecimal("0.15"))
                    .subtract(config.getIrrfDeducaoFaixa3())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Faixa 4: 22.5%
        if (baseCalculo.compareTo(config.getIrrfLimiteFaixa4()) <= 0) {
            return baseCalculo.multiply(new BigDecimal("0.225"))
                    .subtract(config.getIrrfDeducaoFaixa4())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Faixa 5: 27.5%
        return baseCalculo.multiply(new BigDecimal("0.275"))
                .subtract(config.getIrrfDeducaoFaixa5())
                .setScale(2, RoundingMode.HALF_UP);
    }

    private ItemPagamento criarItem(String nome, TipoItemPagamento tipo, BigDecimal valor, Pagamento pg) {
        return ItemPagamento.builder()
                .nome(nome)
                .tipo(tipo)
                .valor(valor.setScale(2, RoundingMode.HALF_UP))
                .pagamento(pg)
                .build();
    }
}