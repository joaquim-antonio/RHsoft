package com.exemplo.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;
import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.exemplo.app.repository.FaixaInssRepository;

@Service
public class CalculadoraFolhaService {

    @Autowired 
    private FaixaInssRepository faixaInssRepository;

    /**
     * Processa o cálculo completo da folha de um funcionário.
     */
    public void processarFolhaFuncionario(Pagamento pagamento, Funcionario func, ConfiguracaoSistema config) {
        List<ItemPagamento> novosItens = new ArrayList<>();
        
        // Calcula quanto vale o salário neste mês específico (se entrou no meio do mês, recebe menos)
        BigDecimal salarioCalculado = calcularSalarioProporcional(func, pagamento);
        BigDecimal totalProventos = BigDecimal.ZERO;
        
        //  PROVENTOS 

        // Salário Base
        novosItens.add(criarItem("Salário Base", TipoItemPagamento.PROVENTO, salarioCalculado, pagamento));
        totalProventos = totalProventos.add(salarioCalculado);
        pagamento.setSalarioBase(salarioCalculado); 

        // Adicionais
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

        //  DESCONTOS 

        // INSS Progressivo (Mantido a lógica de banco de dados)
        BigDecimal inss = calcularInssProgressivo(totalProventos);
        novosItens.add(criarItem("INSS", TipoItemPagamento.DESCONTO, inss, pagamento));

        // IRRF (Sem dedução de dependentes por enquanto)
        BigDecimal baseIRRF = totalProventos.subtract(inss);
        BigDecimal irrf = calcularIRRF(baseIRRF, config);
        
        if (irrf.compareTo(BigDecimal.ZERO) > 0) {
            novosItens.add(criarItem("IRRF", TipoItemPagamento.DESCONTO, irrf, pagamento));
        }

        // Vale Transporte (Baseado no salário calculado/proporcional)
        BigDecimal vt = salarioCalculado.multiply(config.getPercentualValeTransporte());
        novosItens.add(criarItem("Vale Transporte", TipoItemPagamento.DESCONTO, vt, pagamento));
        pagamento.setValeTransporte(vt); 

        // Vale Alimentação
        pagamento.setValeAlimentacao(config.getValorValeAlimentacao());
        
        String nomeCargo = (func.getCargo() != null) ? func.getCargo().getNome() : "Não Informado";
        pagamento.setCbo(nomeCargo);
        pagamento.setMensagens("Cálculo realizado em " + LocalDate.now());

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
     * Útil para meses de admissão.
     */
    private BigDecimal calcularSalarioProporcional(Funcionario func, Pagamento pagamento) {
        YearMonth competenciaFolha = YearMonth.parse(pagamento.getMesAnoReferencia());
        YearMonth competenciaAdmissao = YearMonth.from(func.getDataAdmissao());

        // Se a admissão for no mesmo mês e ano da folha
        if (competenciaFolha.equals(competenciaAdmissao)) {
            int diaAdmissao = func.getDataAdmissao().getDayOfMonth();
            
            // Regra comercial: Dia 31 conta como 30.
            // Ex: Entrou dia 20. Trabalhou: 30 - 20 + 1 = 11 dias.
            int diasTrabalhados = 30 - Math.min(diaAdmissao, 30) + 1;
            
            if (diasTrabalhados < 1) diasTrabalhados = 0; 

            // Cálculo: (Salário / 30) * diasTrabalhados
            return func.getSalario()
                    .divide(new BigDecimal("30"), 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(diasTrabalhados))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Se não for mês de admissão, recebe integral
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
     * Calcula o IRRF usando as alíquotas configuradas no banco de dados.
     */
    private BigDecimal calcularIRRF(BigDecimal base, ConfiguracaoSistema config) {
        BigDecimal baseCalculo = base;

        if (baseCalculo.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        
        // Faixa 1 (Isento)
        if (baseCalculo.compareTo(config.getIrrfLimiteIsento()) <= 0) {
            return BigDecimal.ZERO;
        }

        // Faixa 2
        if (baseCalculo.compareTo(config.getIrrfLimiteFaixa2()) <= 0) {
            return baseCalculo.multiply(config.getIrrfAliquotaFaixa2())
                    .subtract(config.getIrrfDeducaoFaixa2())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Faixa 3
        if (baseCalculo.compareTo(config.getIrrfLimiteFaixa3()) <= 0) {
            return baseCalculo.multiply(config.getIrrfAliquotaFaixa3())
                    .subtract(config.getIrrfDeducaoFaixa3())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Faixa 4
        if (baseCalculo.compareTo(config.getIrrfLimiteFaixa4()) <= 0) {
            return baseCalculo.multiply(config.getIrrfAliquotaFaixa4())
                    .subtract(config.getIrrfDeducaoFaixa4())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Faixa 5 (Teto)
        return baseCalculo.multiply(config.getIrrfAliquotaFaixa5())
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