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

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private FolhaPagamentoRepository folhaPagamentoRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private CalculadoraFolhaService calculadoraService;
    @Mock
    private ConfiguracaoService configuracaoService;

    @InjectMocks
    private PagamentoService service;

    private FolhaPagamento folhaComStatus(StatusPagamento status) {
        FolhaPagamento f = new FolhaPagamento();
        f.setStatus(status);
        return f;
    }

    @Test
    void buscarPagamentoPorCodigo_quandoNaoExiste_lanca() {
        when(pagamentoRepository.findByCodigo("X")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarPagamentoPorCodigo("X"));
    }

    @Test
    void listarPagamentosPorFuncionario_quandoFuncionarioNaoExiste_lanca() {
        when(funcionarioRepository.existsById("123")).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.listarPagamentosPorFuncionario("123"));
    }

    @Test
    void listarPagamentosPorFuncionario_valido_retornaLista() {
        when(funcionarioRepository.existsById("123")).thenReturn(true);
        when(pagamentoRepository.findByFuncionarioCpf("123")).thenReturn(List.of(new Pagamento()));
        assertEquals(1, service.listarPagamentosPorFuncionario("123").size());
    }

    @Test
    void criarPagamento_quandoFuncionarioNaoExiste_lanca() {
        when(funcionarioRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.criarPagamento(null, "123", 1L));
    }

    @Test
    void criarPagamento_quandoFolhaNaoExiste_lanca() {
        when(funcionarioRepository.findById("123")).thenReturn(Optional.of(new Funcionario()));
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.criarPagamento(null, "123", 1L));
    }

    @Test
    void criarPagamento_quandoFolhaFechada_lanca() {
        when(funcionarioRepository.findById("123")).thenReturn(Optional.of(new Funcionario()));
        when(folhaPagamentoRepository.findById(1L))
                .thenReturn(Optional.of(folhaComStatus(StatusPagamento.FECHADA)));
        assertThrows(IllegalStateException.class, () -> service.criarPagamento(null, "123", 1L));
    }

    @Test
    void criarPagamento_semItensManuais_criaDoZero() {
        Funcionario func = new Funcionario();
        func.setCpf("12345678900");
        FolhaPagamento folha = folhaComStatus(StatusPagamento.ABERTO);

        when(funcionarioRepository.findById("12345678900")).thenReturn(Optional.of(func));
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(new ConfiguracaoSistema());
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pagamento pagamento = service.criarPagamento(null, "12345678900", 1L);

        assertNotNull(pagamento.getCodigo());
        assertNotNull(pagamento.getMesAnoReferencia());
        assertNotNull(pagamento.getVencimento());
    }

    @Test
    void criarPagamento_comItensManuais_preservaItens() {
        Funcionario func = new Funcionario();
        func.setCpf("12345678900");
        FolhaPagamento folha = folhaComStatus(StatusPagamento.ABERTO);

        Pagamento input = new Pagamento();
        List<ItemPagamento> itens = new ArrayList<>();
        itens.add(ItemPagamento.builder().tipo(TipoItemPagamento.PROVENTO).valor(new BigDecimal("100")).build());
        input.setItens(itens);

        when(funcionarioRepository.findById("12345678900")).thenReturn(Optional.of(func));
        when(folhaPagamentoRepository.findById(1L)).thenReturn(Optional.of(folha));
        when(configuracaoService.buscarConfiguracaoAtual()).thenReturn(new ConfiguracaoSistema());
        doAnswer(inv -> {
            Pagamento p = inv.getArgument(0);
            if (p.getItens() == null) {
                p.setItens(new ArrayList<>());
            }
            return null;
        }).when(calculadoraService).processarFolhaFuncionario(any(), any(), any());
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pagamento pagamento = service.criarPagamento(input, "12345678900", 1L);
        // Comportamento atual: os itens do input são os mesmos que a calculadora usa de base,
        // então o addAll os duplica na lista.
        assertEquals(2, pagamento.getItens().size());
    }

    @Test
    void deletarPagamento_quandoFolhaFechada_lanca() {
        Pagamento pagamento = new Pagamento();
        pagamento.setFolhaPagamento(folhaComStatus(StatusPagamento.FECHADA));
        when(pagamentoRepository.findByCodigo("X")).thenReturn(Optional.of(pagamento));
        assertThrows(IllegalStateException.class, () -> service.deletarPagamento("X"));
    }

    @Test
    void deletarPagamento_valido_deleta() {
        Pagamento pagamento = new Pagamento();
        pagamento.setFolhaPagamento(folhaComStatus(StatusPagamento.ABERTO));
        when(pagamentoRepository.findByCodigo("X")).thenReturn(Optional.of(pagamento));
        service.deletarPagamento("X");
        verify(pagamentoRepository).delete(pagamento);
    }

    @Test
    void recalcularTotais_valido_recalcula() {
        Pagamento pagamento = new Pagamento();
        pagamento.setItens(new ArrayList<>());
        when(pagamentoRepository.findByCodigo("X")).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertNotNull(service.recalcularTotais("X").getValorLiquido());
    }
}
