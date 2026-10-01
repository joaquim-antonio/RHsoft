package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.dto.RequestVagaDTO;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.VagaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class VagaServiceTest {

    @Mock
    private DepartamentoRepository departamentoRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private VagaRepository vagaRepository;

    @InjectMocks
    private VagaService vagaService;

    private RequestVagaDTO dtoValido() {
        return new RequestVagaDTO("Analista", "Analista Pleno", "Descricao",
                LocalDate.now().plusDays(10), 1L, 2L);
    }

    @Test
    void listarTodasVagas_retornaLista() {
        when(vagaRepository.findAll()).thenReturn(List.of(new Vaga()));
        assertEquals(1, vagaService.listarTodasVagas().size());
    }

    @Test
    void listarVagasDisponiveis_filtraVagasExpiradas() {
        Vaga valida = new Vaga();
        valida.setDataLimite(LocalDate.now().plusDays(5));
        Vaga expirada = new Vaga();
        expirada.setDataLimite(LocalDate.now().minusDays(1));
        when(vagaRepository.findAll()).thenReturn(List.of(valida, expirada));
        assertEquals(1, vagaService.listarVagasDisponiveis().size());
    }

    @Test
    void buscarVagaPorId_quandoNaoExiste_lancaExcecao() {
        when(vagaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vagaService.buscarVagaPorId(1L));
    }

    @Test
    void criarVaga_comDadosValidos_salva() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(new Cargo()));
        when(departamentoRepository.findById(2L)).thenReturn(Optional.of(new Departamento()));
        when(vagaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Vaga vaga = vagaService.criarVaga(dtoValido());
        assertEquals("Analista Pleno", vaga.getTitulo());
        verify(vagaRepository).save(any());
    }

    @Test
    void criarVaga_comDataPassada_lancaExcecao() {
        RequestVagaDTO dto = new RequestVagaDTO("A", "B", "C", LocalDate.now().minusDays(1), 1L, 2L);
        assertThrows(IllegalArgumentException.class, () -> vagaService.criarVaga(dto));
    }

    @Test
    void criarVaga_cargoInexistente_lancaExcecao() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vagaService.criarVaga(dtoValido()));
    }

    @Test
    void criarVaga_departamentoInexistente_lancaExcecao() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(new Cargo()));
        when(departamentoRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vagaService.criarVaga(dtoValido()));
    }
}
