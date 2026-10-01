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

import com.exemplo.app.model.Departamento;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class DepartamentoServiceTest {

    @Mock
    private DepartamentoRepository departamentoRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private DepartamentoService departamentoService;

    private Departamento depto() {
        Departamento d = new Departamento(1L, "TI", "Tecnologia");
        return d;
    }

    @Test
    void listarTodosDepartamentos_retornaLista() {
        when(departamentoRepository.findAll()).thenReturn(List.of(depto()));
        assertEquals(1, departamentoService.listarTodosDepartamentos().size());
    }

    @Test
    void buscarDepartamentoPorCodigo_quandoNaoExiste_lancaExcecao() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> departamentoService.buscarDepartamentoPorCodigo(1L));
    }

    @Test
    void criarDepartamento_quandoNomeExiste_lancaExcecao() {
        when(departamentoRepository.findByNome("TI")).thenReturn(Optional.of(depto()));
        assertThrows(IllegalArgumentException.class, () -> departamentoService.criarDepartamento(depto()));
    }

    @Test
    void criarDepartamento_quandoCodigoExiste_lancaExcecao() {
        when(departamentoRepository.findByNome("TI")).thenReturn(Optional.empty());
        when(departamentoRepository.existsByCodigo(1L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> departamentoService.criarDepartamento(depto()));
    }

    @Test
    void criarDepartamento_quandoValido_salva() {
        when(departamentoRepository.findByNome("TI")).thenReturn(Optional.empty());
        when(departamentoRepository.existsByCodigo(1L)).thenReturn(false);
        when(departamentoRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals("TI", departamentoService.criarDepartamento(depto()).getNome());
    }

    @Test
    void registrarDepartamento_quandoExiste_retornaExistente() {
        Departamento existente = depto();
        when(departamentoRepository.findByNome("TI")).thenReturn(Optional.of(existente));
        assertSame(existente, departamentoService.registrarDepartamento(depto()));
    }

    @Test
    void atualizarDepartamento_quandoNomeEmUsoPorOutro_lancaExcecao() {
        Departamento existente = depto();
        Departamento outro = new Departamento(2L, "RH", null);
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(departamentoRepository.findByNome("RH")).thenReturn(Optional.of(outro));

        Departamento novo = new Departamento();
        novo.setNome("RH");
        assertThrows(IllegalArgumentException.class, () -> departamentoService.atualizarDepartamento(1L, novo));
    }

    @Test
    void atualizarDepartamento_valido_atualiza() {
        Departamento existente = depto();
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(departamentoRepository.findByNome("ERP")).thenReturn(Optional.empty());
        when(departamentoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Departamento novo = new Departamento();
        novo.setNome("ERP");
        novo.setDescricao("Financeiro");
        assertEquals("ERP", departamentoService.atualizarDepartamento(1L, novo).getNome());
    }

    @Test
    void deletarDepartamento_quandoEmUso_lancaExcecao() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(depto()));
        when(funcionarioRepository.existsByDepartamentoCodigo(1L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> departamentoService.deletarDepartamento(1L));
    }

    @Test
    void deletarDepartamento_quandoLivre_deleta() {
        Departamento d = depto();
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(d));
        when(funcionarioRepository.existsByDepartamentoCodigo(1L)).thenReturn(false);
        departamentoService.deletarDepartamento(1L);
        verify(departamentoRepository).delete(d);
    }
}
