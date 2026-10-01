package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Pessoa;
import com.exemplo.app.repository.EnderecoRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    EnderecoRepository enderecoRepository;
    @Mock
    PessoaRepository pessoaRepository;

    @InjectMocks
    EnderecoService service;

    @Test
    void buscarPorCpf_quandoPessoaNaoExiste_lanca() {
        when(pessoaRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarEnderecoPorCPF("123"));
    }

    @Test
    void buscarPorCpf_quandoExiste_retornaEndereco() {
        Pessoa p = new Pessoa();
        Endereco e = new Endereco();
        p.setEndereco(e);
        when(pessoaRepository.findById("123")).thenReturn(Optional.of(p));
        assertSame(e, service.buscarEnderecoPorCPF("123"));
    }

    @Test
    void atualizar_quandoPessoaNaoExiste_lanca() {
        when(pessoaRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.atualizarEnderecoDePessoa("123", new Endereco()));
    }

    @Test
    void atualizar_quandoExiste_salvaCamposNovos() {
        Pessoa p = new Pessoa();
        Endereco existente = new Endereco();
        p.setEndereco(existente);
        Endereco novo = new Endereco();
        novo.setCep("30100-000");
        novo.setCidade("BH");

        when(pessoaRepository.findById("123")).thenReturn(Optional.of(p));
        when(enderecoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Endereco resultado = service.atualizarEnderecoDePessoa("123", novo);
        assertEquals("30100-000", resultado.getCep());
        assertEquals("BH", resultado.getCidade());
    }
}
