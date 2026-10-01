package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.repository.ContaBancariaRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ContaBancariaServiceTest {

    @Mock
    ContaBancariaRepository contaRepo;
    @Mock
    FuncionarioRepository funcRepo;

    @InjectMocks
    ContaBancariaService service;

    private Funcionario funcionarioComCpf(String cpf) {
        Funcionario f = new Funcionario();
        f.setCpf(cpf);
        return f;
    }

    @Test
    void buscarPorId_quandoNaoExiste_lanca() {
        when(contaRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarContaBancariaPorId(1L));
    }

    @Test
    void buscarPorFuncionario_quandoFuncionarioNaoExiste_lanca() {
        when(funcRepo.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarContaBancariaPorFuncionario("123"));
    }

    @Test
    void buscarPorFuncionario_quandoContaNaoExiste_lanca() {
        Funcionario f = funcionarioComCpf("123");
        when(funcRepo.findById("123")).thenReturn(Optional.of(f));
        when(contaRepo.findByFuncionario(f)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarContaBancariaPorFuncionario("123"));
    }

    @Test
    void criar_quandoJaPossuiConta_lancaIllegalState() {
        Funcionario f = funcionarioComCpf("123");
        when(funcRepo.findById("123")).thenReturn(Optional.of(f));
        when(contaRepo.findByFuncionario(f)).thenReturn(Optional.of(new ContaBancaria()));
        assertThrows(IllegalStateException.class, () -> service.criarContaBancaria(new ContaBancaria(), "123"));
    }

    @Test
    void criar_quandoValido_salva() {
        Funcionario f = funcionarioComCpf("123");
        ContaBancaria conta = new ContaBancaria();
        when(funcRepo.findById("123")).thenReturn(Optional.of(f));
        when(contaRepo.findByFuncionario(f)).thenReturn(Optional.empty());
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        ContaBancaria salva = service.criarContaBancaria(conta, "123");
        assertSame(f, salva.getFuncionario());
    }

    @Test
    void atualizarComCpfDivergente_lancaIllegalState() {
        ContaBancaria conta = new ContaBancaria();
        conta.setFuncionario(funcionarioComCpf("111"));
        when(contaRepo.findById(1L)).thenReturn(Optional.of(conta));
        assertThrows(IllegalStateException.class,
                () -> service.atualizarContaBancaria(1L, "0001", "123", "Banco", "pix", "222"));
    }

    @Test
    void atualizar_valido_atualizaCampos() {
        ContaBancaria conta = new ContaBancaria();
        conta.setFuncionario(funcionarioComCpf("111"));
        when(contaRepo.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        ContaBancaria atualizada = service.atualizarContaBancaria(1L, "0001", "999", "Nubank", "pix@x", "111");
        assertEquals("Nubank", atualizada.getNomeBanco());
        assertEquals("999", atualizada.getNumero());
    }

    @Test
    void atualizarChavePix_valido_atualiza() {
        ContaBancaria conta = new ContaBancaria();
        when(contaRepo.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals("nova", service.atualizarChavePix(1L, "nova").getChavePix());
    }

    @Test
    void deletar_quandoNaoExiste_lanca() {
        when(contaRepo.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.deletarContaBancaria(1L));
    }

    @Test
    void deletar_quandoExiste_deleta() {
        when(contaRepo.existsById(1L)).thenReturn(true);
        service.deletarContaBancaria(1L);
        verify(contaRepo).deleteById(1L);
    }
}
