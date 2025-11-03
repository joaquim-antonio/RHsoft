package com.exemplo.app.service;

import com.exemplo.app.model.Departamento;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartamentoServiceTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private DepartamentoService departamentoService;

    private Departamento departamento;

    @BeforeEach
    void setUp() {
        departamento = new Departamento(1L, "RH", "Recursos Humanos");
    }

    @Test
    void listarTodosDepartamentos_DeveRetornarListaDeDepartamentos() {
        List<Departamento> listaEsperada = Arrays.asList(departamento, new Departamento(2L, "TI", "Tecnologia da Informação"));
        when(departamentoRepository.findAll()).thenReturn(listaEsperada);

        List<Departamento> resultado = departamentoService.listarTodosDepartamentos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(departamentoRepository, times(1)).findAll();
    }

    @Test
    void buscarDepartamentoPorCodigo_DeveRetornarDepartamento_QuandoEncontrado() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(departamento));

        Departamento resultado = departamentoService.buscarDepartamentoPorCodigo(1L);

        assertNotNull(resultado);
        assertEquals("RH", resultado.getNome());
        verify(departamentoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarDepartamentoPorCodigo_DeveLancarExcecao_QuandoNaoEncontrado() {
        when(departamentoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> departamentoService.buscarDepartamentoPorCodigo(2L));
        verify(departamentoRepository, times(1)).findById(2L);
    }

    @Test
    void criarDepartamento_DeveCriarNovoDepartamento_QuandoNaoExistente() {
        Departamento novoDepartamento = new Departamento(null, "Financeiro", "Setor Financeiro");
        when(departamentoRepository.findByNome("Financeiro")).thenReturn(Optional.empty());
        when(departamentoRepository.existsByCodigo(any())).thenReturn(false);
        when(departamentoRepository.saveAndFlush(novoDepartamento)).thenReturn(departamento);

        Departamento resultado = departamentoService.criarDepartamento(novoDepartamento);

        assertNotNull(resultado);
        assertEquals("RH", resultado.getNome());
        verify(departamentoRepository, times(1)).saveAndFlush(novoDepartamento);
    }

    @Test
    void criarDepartamento_DeveLancarExcecao_QuandoNomeJaExiste() {
        Departamento novoDepartamento = new Departamento(null, "RH", "Recursos Humanos");
        when(departamentoRepository.findByNome("RH")).thenReturn(Optional.of(departamento));

        assertThrows(IllegalArgumentException.class, () -> departamentoService.criarDepartamento(novoDepartamento));
        verify(departamentoRepository, never()).saveAndFlush(any());
    }

    @Test
    void atualizarDepartamento_DeveAtualizarDepartamento_QuandoValido() {
        Departamento departamentoAtualizado = new Departamento(1L, "RH Atualizado", "Nova Descrição");
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(departamento));
        when(departamentoRepository.findByNome("RH Atualizado")).thenReturn(Optional.empty());
        when(departamentoRepository.save(any(Departamento.class))).thenReturn(departamentoAtualizado);

        Departamento resultado = departamentoService.atualizarDepartamento(1L, departamentoAtualizado);

        assertNotNull(resultado);
        assertEquals("RH Atualizado", resultado.getNome());
        assertEquals("Nova Descrição", resultado.getDescricao());
        verify(departamentoRepository, times(1)).save(departamento);
    }

    @Test
    void deletarDepartamento_DeveDeletarDepartamento_QuandoNaoEstiverEmUso() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(departamento));
        when(funcionarioRepository.existsByDepartamentoCodigo(1L)).thenReturn(false);
        doNothing().when(departamentoRepository).delete(departamento);

        assertDoesNotThrow(() -> departamentoService.deletarDepartamento(1L));
        verify(departamentoRepository, times(1)).delete(departamento);
    }

    @Test
    void deletarDepartamento_DeveLancarExcecao_QuandoEstiverEmUso() {
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(departamento));
        when(funcionarioRepository.existsByDepartamentoCodigo(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> departamentoService.deletarDepartamento(1L));
        verify(departamentoRepository, never()).delete(any());
    }
}
