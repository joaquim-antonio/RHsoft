package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.dto.EditarPagamentoDto;
import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.AdministradorRepository;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class FolhaPagamentoServiceTest {

    @Mock
    private FuncionarioService funcionarioService;
    @Mock
    private FolhaPagamentoRepository folhaPagamentoRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private ConfiguracaoService configuracaoService;
    @Mock
    private CalculadoraFolhaService calculadoraService;
    @Mock
    private AdministradorRepository administradorRepository;

    @InjectMocks
    private FolhaPagamentoService service;

    private FolhaPagamento folhaComStatus(StatusPagamento status) {
        FolhaPagamento f = new FolhaPagamento();
        f.setStatus(status);
        f.setPagamentos(new ArrayList<>());
        return f;
    }

    private ConfiguracaoSistema configPadrao() {
        ConfiguracaoSistema c = new ConfiguracaoSistema();
        c.setDiaFechamentoMensal(25);
        c.setDiasLimiteReabertura(5);
        return c;
    }

    // ---------- listar / buscar ----------

    @Test
    void listarTodasDTO_retornaLista() {
        when(folhaPagamentoRepository.findAll()).thenReturn(List.of(folhaComStatus(StatusPagamento.ABERTO)));
        assertEquals(1, service.listarTodasDTO().size());
    }

    @Test
    void buscarFolhaPorId_quandoNaoExiste_lanca() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarFolhaPorId(1L));
    }

    @Test
    void buscarPagamentoPorCodigo_quandoNaoExiste_lanca() {
        when(pagamentoRepository.findByCodigo("X")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarPagamentoPorCodigo("X"));
    }

    // ---------- geração da folha ----------

    @Test
    void gerarFolha_quandoFechada_lanca() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folhaComStatus(StatusPagamento.FECHADA)));
        assertThrows(RuntimeException.class, () -> service.gerarFolhaDePagamento(1L));
    }

    @Test
    void gerarFolha_comFuncionarios_geraPagamentos() {
        FolhaPagamento folha = folhaComStatus(StatusPagamento.ABERTO);

        Funcionario func = new Funcionario();
        func.setCpf("12345678900");
        func.setSalario(new BigDecimal("3000"));
        func.setHorasTrabalhadas(40.0);

        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        when(funcionarioService.listarTodosFuncionarios()).thenReturn(List.of(func));
        when(pagamentoRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        service.gerarFolhaDePagamento(1L);

        verify(calculadoraService).processarFolhaFuncionario(any(), eq(func), any());
        verify(pagamentoRepository).saveAll(any());
    }

    @Test
    void gerarFolha_comPagamentosAntigos_limpaAntes() {
        FolhaPagamento folha = folhaComStatus(StatusPagamento.ABERTO);
        Pagamento antigo = new Pagamento();
        folha.getPagamentos().add(antigo);

        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        when(funcionarioService.listarTodosFuncionarios()).thenReturn(List.of());

        service.gerarFolhaDePagamento(1L);

        verify(pagamentoRepository).deleteAll(List.of(antigo));
    }

    // ---------- edição de pagamento ----------

    @Test
    void editarPagamento_quandoFolhaFechada_lanca() {
        Pagamento pagamento = new Pagamento();
        pagamento.setFolhaPagamento(folhaComStatus(StatusPagamento.FECHADA));
        when(pagamentoRepository.findByCodigo("X")).thenReturn(Optional.of(pagamento));

        assertThrows(RuntimeException.class, () -> service.editarPagamento(new EditarPagamentoDto(null, null, "X")));
    }

    @Test
    void editarPagamento_comHorasExtrasEAadicional_adicionaItens() {
        Funcionario func = new Funcionario();
        func.setSalario(new BigDecimal("3000"));
        func.setHorasTrabalhadas(40.0);

        Pagamento pagamento = new Pagamento();
        pagamento.setFuncionario(func);
        pagamento.setItens(new ArrayList<>());
        pagamento.setFolhaPagamento(folhaComStatus(StatusPagamento.ABERTO));

        when(pagamentoRepository.findByCodigo("X")).thenReturn(Optional.of(pagamento));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pagamento resultado = service.editarPagamento(
                new EditarPagamentoDto(10.0, new BigDecimal("150"), "X"));

        assertNotNull(resultado.getHorasExtras());
        assertEquals(2, resultado.getItens().size());
    }

    // ---------- fluxo da folha ----------

    @Test
    void abrirFolha_quandoJaExisteAberta_lanca() {
        when(folhaPagamentoRepository.existsByStatus(StatusPagamento.ABERTO)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> service.abrirFolha("111"));
    }

    @Test
    void abrirFolha_quandoAdminNaoExiste_lanca() {
        when(folhaPagamentoRepository.existsByStatus(StatusPagamento.ABERTO)).thenReturn(false);
        when(administradorRepository.findByCpf("111")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.abrirFolha("111"));
    }

    @Test
    void abrirFolha_valido_criaFolhaAberta() {
        when(folhaPagamentoRepository.existsByStatus(StatusPagamento.ABERTO)).thenReturn(false);
        when(administradorRepository.findByCpf("111")).thenReturn(Optional.of(new Administrador()));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        when(folhaPagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FolhaPagamento folha = service.abrirFolha("111");
        assertEquals(StatusPagamento.ABERTO, folha.getStatus());
        assertEquals(0, folha.getTotalLiquido().compareTo(BigDecimal.ZERO));
    }

    @Test
    void fecharFolha_quandoNaoAberta_lanca() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folhaComStatus(StatusPagamento.FECHADA)));
        assertThrows(RuntimeException.class, () -> service.fecharFolha(1L));
    }

    @Test
    void fecharFolha_valido_somaLiquidos() {
        FolhaPagamento folha = folhaComStatus(StatusPagamento.ABERTO);
        Pagamento p1 = new Pagamento();
        p1.setValorLiquido(new BigDecimal("1000"));
        Pagamento p2 = new Pagamento();
        p2.setValorLiquido(new BigDecimal("500"));
        folha.getPagamentos().add(p1);
        folha.getPagamentos().add(p2);

        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        when(folhaPagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FolhaPagamento fechada = service.fecharFolha(1L);
        assertEquals(StatusPagamento.FECHADA, fechada.getStatus());
        assertEquals(0, fechada.getTotalLiquido().compareTo(new BigDecimal("1500")));
    }

    @Test
    void consolidarFolha_quandoNaoFechada_lanca() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folhaComStatus(StatusPagamento.ABERTO)));
        assertThrows(RuntimeException.class, () -> service.consolidarFolha(1L));
    }

    @Test
    void consolidarFolha_valido_consolida() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folhaComStatus(StatusPagamento.FECHADA)));
        when(folhaPagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals(StatusPagamento.CONSOLIDADA, service.consolidarFolha(1L).getStatus());
    }

    @Test
    void reabrirFolha_quandoJaAberta_lanca() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folhaComStatus(StatusPagamento.ABERTO)));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        assertThrows(RuntimeException.class, () -> service.reabrirFolha(1L));
    }

    @Test
    void reabrirFolha_consolidadaForaDoPrazo_lanca() {
        FolhaPagamento folha = folhaComStatus(StatusPagamento.CONSOLIDADA);
        folha.setDataFechamento(LocalDate.now().minusDays(30));
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        assertThrows(RuntimeException.class, () -> service.reabrirFolha(1L));
    }

    @Test
    void reabrirFolha_consolidadaDentroDoPrazo_reabre() {
        FolhaPagamento folha = folhaComStatus(StatusPagamento.CONSOLIDADA);
        folha.setDataFechamento(LocalDate.now().minusDays(2));
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(configPadrao());
        when(folhaPagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals(StatusPagamento.ABERTO, service.reabrirFolha(1L).getStatus());
    }

    @Test
    void enviarFolha_quandoAberta_lanca() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folhaComStatus(StatusPagamento.ABERTO)));
        assertThrows(IllegalStateException.class, () -> service.enviarFolhaParaFuncionarios(1L));
    }

    @Test
    void enviarFolha_quandoFechada_naoLanca() {
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folhaComStatus(StatusPagamento.FECHADA)));
        assertDoesNotThrow(() -> service.enviarFolhaParaFuncionarios(1L));
    }
}
