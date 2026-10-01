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

import com.exemplo.app.exception.RegraNegocioException;
import com.exemplo.app.model.Pessoa;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.PessoaRepository;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private PessoaService pessoaService;

    @Test
    void listarTodasPessoas_deveRetornarListaDoRepositorio() {
        when(pessoaRepository.findAll()).thenReturn(List.of(new Pessoa(), new Pessoa()));
        assertEquals(2, pessoaService.listarTodasPessoas().size());
    }

    @Test
    void buscarPessoaCPF_deveDelegarAoRepositorio() {
        Pessoa p = new Pessoa();
        when(pessoaRepository.findById("123")).thenReturn(Optional.of(p));
        assertTrue(pessoaService.buscarPessoaCPF("123").isPresent());
    }

    @Test
    void salvarPessoa_deveDelegarAoRepositorio() {
        Pessoa p = new Pessoa();
        when(pessoaRepository.save(p)).thenReturn(p);
        assertEquals(p, pessoaService.salvarPessoa(p));
    }

    @Test
    void excluirPessoa_quandoNaoExiste_lancaRegraNegocio() {
        when(pessoaRepository.existsById("123")).thenReturn(false);
        assertThrows(RegraNegocioException.class, () -> pessoaService.excluirPessoa("123"));
    }

    @Test
    void excluirPessoa_quandoExiste_deleta() {
        when(pessoaRepository.existsById("123")).thenReturn(true);
        pessoaService.excluirPessoa("123");
        verify(pessoaRepository).deleteById("123");
    }

    @Test
    void atualizarPessoa_quandoNaoEncontrada_lancaExcecao() {
        when(pessoaRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(RegraNegocioException.class,
                () -> pessoaService.atualizarPessoa("123", new Pessoa()));
    }

    @Test
    void atualizarPessoa_atualizaCamposBasicos() {
        Pessoa existente = new Pessoa();
        existente.setNome("Antigo");
        Pessoa novos = new Pessoa();
        novos.setNome("Novo");
        novos.setTelefone("999");

        when(pessoaRepository.findById("123")).thenReturn(Optional.of(existente));
        when(pessoaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pessoa atualizado = pessoaService.atualizarPessoa("123", novos);
        assertEquals("Novo", atualizado.getNome());
        assertEquals("999", atualizado.getTelefone());
    }
}
