package com.exemplo.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;
import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.model.FaixaIrrf;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;

@Service
public class CalculadoraFolhaService {

    /**
     * Processa o cálculo completo da folha de um funcionário.
     * Agora totalmente desacoplado de Repositories.
     */
    public void processarFolhaFuncionario(Pagamento pagamento, Funcionario func, ConfiguracaoSistema config) {
        List<ItemPagamento> novosItens = new ArrayList<>();
        
        // 1. CÁLCULO PROPORCIONAL (Admissão)
        BigDecimal salarioCalculado = calcularSalarioProporcional(func, pagamento);
        BigDecimal totalProventos = BigDecimal.ZERO;
        
        // --- PROVENTOS ---

        // Salário Base
        novosItens.add(criarItem("Salário Base", TipoItemPagamento.PROVENTO, salarioCalculado, pagamento));
        totalProventos = totalProventos.add(salarioCalculado);
        pagamento.setSalarioBase(salarioCalculado); 

        // Adicionais (Insalubridade / Periculosidade)
        BigDecimal valorAdicional = calcularAdicionais(func, config, salarioCalculado);
        
        if (valorAdicional.compareTo(BigDecimal.ZERO) > 0) {
            String nomeAdicional = (func.getTipoAcrescimo() == TipoAcrescimo.INSALUBRIDADE) ? "Insalubridade" : "Periculosidade";
            novosItens.add(criarItem(nomeAdicional, TipoItemPagamento.PROVENTO, valorAdicional, pagamento));
            totalProventos = totalProventos.add(valorAdicional);
        }

        // Horas Extras (Inicializa zerado se nulo)
        if(pagamento.getHorasExtras() == null) {
            pagamento.setHorasExtras(BigDecimal.ZERO); 
        }

        // --- DESCONTOS ---

        // INSS Progressivo (Usa a lista da config)
        BigDecimal inss = calcularInssProgressivo(totalProventos, config);
        novosItens.add(criarItem("INSS", TipoItemPagamento.DESCONTO, inss, pagamento));

        // IRRF (Usa a lista da config)
        BigDecimal baseIRRF = totalProventos.subtract(inss);
        
        // Descontar dependentes se implementado no futuro:
        // BigDecimal deducaoDependentes = config.getIrrfDeducaoPorDependente().multiply(new BigDecimal(func.getDependentes()));
        // baseIRRF = baseIRRF.subtract(deducaoDependentes);

        BigDecimal irrf = calcularIRRF(baseIRRF, config);
        
        if (irrf.compareTo(BigDecimal.ZERO) > 0) {
            novosItens.add(criarItem("IRRF", TipoItemPagamento.DESCONTO, irrf, pagamento));
        }

        // Vale Transporte
        BigDecimal vt = salarioCalculado.multiply(config.getPercentualValeTransporte());
        novosItens.add(criarItem("Vale Transporte", TipoItemPagamento.DESCONTO, vt, pagamento));
        pagamento.setValeTransporte(vt); 

        // Vale Alimentação
        pagamento.setValeAlimentacao(config.getValorValeAlimentacao());
        
        // Metadados
        String nomeCargo = (func.getCargo() != null) ? func.getCargo().getNome() : "Não Informado";
        pagamento.setCbo(nomeCargo);
        pagamento.setMensagens("Cálculo realizado em " + LocalDate.now());

        // Atualiza a lista
        if (pagamento.getItens() == null) {
            pagamento.setItens(new ArrayList<>(novosItens));
        } else {
            pagamento.getItens().clear(); 
            pagamento.getItens().addAll(novosItens);
        }

        pagamento.calcularTotais(); 
    }

    /**
     * Calcula o salário proporcional base 30 dias (Regra Comercial).
     */
    private BigDecimal calcularSalarioProporcional(Funcionario func, Pagamento pagamento) {
        YearMonth competenciaFolha = YearMonth.parse(pagamento.getMesAnoReferencia());
        YearMonth competenciaAdmissao = YearMonth.from(func.getDataAdmissao());

        if (competenciaFolha.equals(competenciaAdmissao)) {
            int diaAdmissao = func.getDataAdmissao().getDayOfMonth();
            int diasTrabalhados = 30 - Math.min(diaAdmissao, 30) + 1;
            
            if (diasTrabalhados < 1) diasTrabalhados = 0; 

            return func.getSalario()
                    .divide(new BigDecimal("30"), 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(diasTrabalhados))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return func.getSalario();
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

    /**
     * Calcula INSS iterando sobre a lista da configuração.
     */
    private BigDecimal calcularInssProgressivo(BigDecimal salarioBruto, ConfiguracaoSistema config) {
        List<FaixaInss> faixas = config.getFaixasInss();
        if(faixas == null || faixas.isEmpty()) return BigDecimal.ZERO;

        BigDecimal impostoTotal = BigDecimal.ZERO;
        BigDecimal tetoINSS = config.getTetoInss();
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
     * Calcula IRRF iterando sobre a lista da configuração.
     */
    private BigDecimal calcularIRRF(BigDecimal baseCalculo, ConfiguracaoSistema config) {
        if (baseCalculo.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        
        List<FaixaIrrf> faixas = config.getFaixasIrrf();
        if(faixas == null || faixas.isEmpty()) return BigDecimal.ZERO;

        for (FaixaIrrf faixa : faixas) {
            
            // Se tiver limite superior e a base for menor ou igual
            if (faixa.getLimiteSuperior() != null && baseCalculo.compareTo(faixa.getLimiteSuperior()) <= 0) {
                return aplicarAliquotaIrrf(baseCalculo, faixa);
            }
            
            // Se for a última faixa (limite null ou "acima de")
            if (faixa.getLimiteSuperior() == null) {
                return aplicarAliquotaIrrf(baseCalculo, faixa);
            }
        }
        
        return BigDecimal.ZERO; 
    }

    private BigDecimal aplicarAliquotaIrrf(BigDecimal base, FaixaIrrf faixa) {
        BigDecimal valor = base.multiply(faixa.getAliquota())
                .subtract(faixa.getDeducao());
        
        return valor.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
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