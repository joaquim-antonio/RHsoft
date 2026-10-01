package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;
import com.exemplo.app.model.FaixaInss;
import com.exemplo.app.model.FaixaIrrf;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Pagamento;

class CalculadoraFolhaServiceTest {

    private CalculadoraFolhaService calculadora;
    private ConfiguracaoSistema configPadrao;

    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraFolhaService();
        configPadrao = new ConfiguracaoSistema();
        configPadrao.setSalarioMinimoVigente(new BigDecimal("1412.00"));
        configPadrao.setPercentualInsalubridadeMin(new BigDecimal("0.10"));
        configPadrao.setPercentualInsalubridadeMedia(new BigDecimal("0.20"));
        configPadrao.setPercentualInsalubridadeMax(new BigDecimal("0.40"));
        configPadrao.setPercentualPericulosidade(new BigDecimal("0.30"));
        configPadrao.setPercentualValeTransporte(new BigDecimal("0.06"));
        configPadrao.setTetoInss(new BigDecimal("7786.02"));
        configPadrao.setValorValeAlimentacao(new BigDecimal("500.00"));

        // Faixas INSS 2024 simuladas
        FaixaInss f1 = FaixaInss.builder().ordem(1).limiteInferior(new BigDecimal("0.00")).limiteSuperior(new BigDecimal("1412.00")).aliquota(new BigDecimal("0.075")).build();
        FaixaInss f2 = FaixaInss.builder().ordem(2).limiteInferior(new BigDecimal("1412.01")).limiteSuperior(new BigDecimal("2666.68")).aliquota(new BigDecimal("0.09")).build();
        FaixaInss f3 = FaixaInss.builder().ordem(3).limiteInferior(new BigDecimal("2666.69")).limiteSuperior(new BigDecimal("4000.03")).aliquota(new BigDecimal("0.12")).build();
        FaixaInss f4 = FaixaInss.builder().ordem(4).limiteInferior(new BigDecimal("4000.04")).limiteSuperior(new BigDecimal("7786.02")).aliquota(new BigDecimal("0.14")).build();
        configPadrao.setFaixasInss(Arrays.asList(f1, f2, f3, f4));

        // Faixas IRRF simuladas
        FaixaIrrf i1 = FaixaIrrf.builder().ordem(1).limiteSuperior(new BigDecimal("2259.20")).aliquota(BigDecimal.ZERO).deducao(BigDecimal.ZERO).build();
        FaixaIrrf i2 = FaixaIrrf.builder().ordem(2).limiteSuperior(new BigDecimal("2826.65")).aliquota(new BigDecimal("0.075")).deducao(new BigDecimal("169.44")).build();
        FaixaIrrf i3 = FaixaIrrf.builder().ordem(3).limiteSuperior(new BigDecimal("3751.05")).aliquota(new BigDecimal("0.15")).deducao(new BigDecimal("381.44")).build();
        FaixaIrrf i4 = FaixaIrrf.builder().ordem(4).limiteSuperior(new BigDecimal("4664.68")).aliquota(new BigDecimal("0.225")).deducao(new BigDecimal("662.77")).build();
        FaixaIrrf i5 = FaixaIrrf.builder().ordem(5).limiteSuperior(new BigDecimal("999999.00")).aliquota(new BigDecimal("0.275")).deducao(new BigDecimal("896.00")).build();
        configPadrao.setFaixasIrrf(Arrays.asList(i1, i2, i3, i4, i5));
    }

    @Test
    @DisplayName("Deve processar folha de pagamento normal com sucesso")
    void testProcessarFolhaNormal() {
        Funcionario func = new Funcionario();
        func.setSalario(new BigDecimal("3000.00"));
        func.setDataAdmissao(LocalDate.of(2020, 1, 15));
        func.setTipoAcrescimo(TipoAcrescimo.NENHUM);

        Pagamento pagamento = new Pagamento();
        pagamento.setMesAnoReferencia("2026-05");

        calculadora.processarFolhaFuncionario(pagamento, func, configPadrao);

        assertNotNull(pagamento.getSalarioBase());
        assertEquals(0, new BigDecimal("3000.00").compareTo(pagamento.getSalarioBase()));
        assertNotNull(pagamento.getItens());
        assertFalse(pagamento.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve processar salário proporcional na admissão no mesmo mês")
    void testSalarioProporcionalAdmissao() {
        Funcionario func = new Funcionario();
        func.setSalario(new BigDecimal("3000.00"));
        func.setDataAdmissao(LocalDate.of(2026, 5, 16)); // 16 de maio (15 dias trabalhados: de 16 a 30)
        func.setTipoAcrescimo(TipoAcrescimo.NENHUM);

        Pagamento pagamento = new Pagamento();
        pagamento.setMesAnoReferencia("2026-05");

        calculadora.processarFolhaFuncionario(pagamento, func, configPadrao);

        // 30 - 16 + 1 = 15 dias -> (3000 / 30) * 15 = 1500.00
        assertEquals(0, new BigDecimal("1500.00").compareTo(pagamento.getSalarioBase()));
    }

    @Test
    @DisplayName("Deve calcular adicionais de Insalubridade e Periculosidade corretamente")
    void testAdicionais() {
        Funcionario funcInsalubre = new Funcionario();
        funcInsalubre.setSalario(new BigDecimal("2000.00"));
        funcInsalubre.setDataAdmissao(LocalDate.of(2020, 1, 1));
        funcInsalubre.setTipoAcrescimo(TipoAcrescimo.INSALUBRIDADE);
        funcInsalubre.setTipoInsalubridade(TipoInsalubridade.MEDIO); // 20% do salário mínimo (1412) = 282.40

        Pagamento pag1 = new Pagamento();
        pag1.setMesAnoReferencia("2026-05");
        calculadora.processarFolhaFuncionario(pag1, funcInsalubre, configPadrao);
        
        boolean temInsalubridade = pag1.getItens().stream().anyMatch(i -> i.getNome() != null && i.getNome().equals("Insalubridade"));
        assertTrue(temInsalubridade);

        Funcionario funcPericuloso = new Funcionario();
        funcPericuloso.setSalario(new BigDecimal("3000.00"));
        funcPericuloso.setDataAdmissao(LocalDate.of(2020, 1, 1));
        funcPericuloso.setTipoAcrescimo(TipoAcrescimo.PERICULOSIDADE); // 30% do salário base (3000) = 900.00

        Pagamento pag2 = new Pagamento();
        pag2.setMesAnoReferencia("2026-05");
        calculadora.processarFolhaFuncionario(pag2, funcPericuloso, configPadrao);

        boolean temPericulosidade = pag2.getItens().stream().anyMatch(i -> i.getNome() != null && i.getNome().equals("Periculosidade"));
        assertTrue(temPericulosidade);
    }
}
