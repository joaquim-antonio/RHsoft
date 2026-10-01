package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.model.Comunicado;
import com.exemplo.app.repository.ComunicadoRepository;

@ExtendWith(MockitoExtension.class)
class ComunicadoServiceTest {

    @Mock
    ComunicadoRepository repo;

    @InjectMocks
    ComunicadoService service;

    @Test
    void obterPorId_quandoExiste_retorna() {
        Comunicado c = new Comunicado();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        assertSame(c, service.obterComunicadoPorId(1L));
    }

    @Test
    void obterPorId_quandoNaoExiste_lanca() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.obterComunicadoPorId(1L));
    }

    @Test
    void criar_defineDataESalva() {
        Comunicado c = new Comunicado();
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        Comunicado salvo = service.criarComunicado(c);
        assertNotNull(salvo.getDataPublicacao());
    }

    @Test
    void atualizar_quandoNaoExiste_lanca() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.atualizarComunicado(1L, new Comunicado()));
    }

    @Test
    void atualizar_quandoExiste_atualizaCampos() {
        Comunicado existente = new Comunicado();
        Comunicado novo = new Comunicado();
        novo.setTitulo("Novo");
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals("Novo", service.atualizarComunicado(1L, novo).getTitulo());
    }

    @Test
    void deletar_quandoNaoExiste_lanca() {
        when(repo.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.deletarComunicado(1L));
    }

    @Test
    void deletar_quandoExiste_deleta() {
        when(repo.existsById(1L)).thenReturn(true);
        service.deletarComunicado(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void listarRecentes_delegaAoRepositorio() {
        when(repo.findTop5ByOrderByDataPublicacaoDesc()).thenReturn(List.of(new Comunicado()));
        assertEquals(1, service.listarRecentes().size());
    }
}
