package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.ItemPagamentoRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ItemPagamentoServiceTest {

    @Mock
    PagamentoRepository pagamentoRepository;
    @Mock
    ItemPagamentoRepository itemPagamentoRepository;

    @InjectMocks
    ItemPagamentoService service;

    @Test
    void buscarItemPorId_quandoNaoExiste_lanca() {
        when(itemPagamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarItemPorId(1L));
    }

    @Test
    void adicionarItem_quandoPagamentoNaoExiste_lanca() {
        when(pagamentoRepository.findByCodigo("ABC")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.adicionarItemAoPagamento("ABC", new ItemPagamento()));
    }

    @Test
    void adicionarItem_quandoValido_adicionaESalva() {
        Pagamento pag = new Pagamento();
        pag.setItens(new ArrayList<>());
        ItemPagamento item = ItemPagamento.builder()
                .nome("Bônus")
                .tipo(TipoItemPagamento.PROVENTO)
                .valor(new BigDecimal("100.00"))
                .build();

        when(pagamentoRepository.findByCodigo("ABC")).thenReturn(Optional.of(pag));
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pagamento resultado = service.adicionarItemAoPagamento("ABC", item);
        assertEquals(1, resultado.getItens().size());
        verify(pagamentoRepository).save(pag);
    }

    @Test
    void removerItem_quandoDeOutroPagamento_lanca() {
        Pagamento pag = new Pagamento();
        pag.setItens(new ArrayList<>());
        Pagamento outroPag = new Pagamento();
        ItemPagamento item = ItemPagamento.builder()
                .tipo(TipoItemPagamento.PROVENTO)
                .valor(new BigDecimal("50.00"))
                .pagamento(outroPag)
                .build();

        when(pagamentoRepository.findByCodigo("ABC")).thenReturn(Optional.of(pag));
        when(itemPagamentoRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class,
                () -> service.removerItemDoPagamento("ABC", 1L));
    }

    @Test
    void removerItem_quandoValido_removeESalva() {
        Pagamento pag = new Pagamento();
        ArrayList<ItemPagamento> itens = new ArrayList<>();
        pag.setItens(itens);
        ItemPagamento item = ItemPagamento.builder()
                .tipo(TipoItemPagamento.PROVENTO)
                .valor(new BigDecimal("50.00"))
                .pagamento(pag)
                .build();
        itens.add(item);

        when(pagamentoRepository.findByCodigo("ABC")).thenReturn(Optional.of(pag));
        when(itemPagamentoRepository.findById(1L)).thenReturn(Optional.of(item));
        when(pagamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pagamento resultado = service.removerItemDoPagamento("ABC", 1L);
        assertTrue(resultado.getItens().isEmpty());
    }
}
