package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.dto.DadosContratacaoDTO;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Candidatura;
import com.exemplo.app.model.Enums.StatusCandidatura;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.repository.CandidatoRepository;
import com.exemplo.app.repository.CandidaturaRepository;
import com.exemplo.app.repository.VagaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CandidaturaServiceTest {

    @Mock
    CandidaturaRepository candidaturaRepository;
    @Mock
    CandidatoRepository candidatoRepository;
    @Mock
    VagaRepository vagaRepository;
    @Mock
    FuncionarioService funcionarioService;

    @InjectMocks
    CandidaturaService service;

    private Vaga vagaValida() {
        Vaga v = new Vaga();
        v.setDataLimite(LocalDate.now().plusDays(5));
        return v;
    }

    @Test
    void aplicar_quandoCandidatoNaoExiste_lanca() {
        when(candidatoRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.aplicarParaVaga("123", 1L));
    }

    @Test
    void aplicar_quandoVagaNaoExiste_lanca() {
        when(candidatoRepository.findById("123")).thenReturn(Optional.of(new Candidato()));
        when(vagaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.aplicarParaVaga("123", 1L));
    }

    @Test
    void aplicar_quandoVagaExpirada_lanca() {
        Vaga v = new Vaga();
        v.setDataLimite(LocalDate.now().minusDays(1));
        when(candidatoRepository.findById("123")).thenReturn(Optional.of(new Candidato()));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(v));
        assertThrows(IllegalStateException.class, () -> service.aplicarParaVaga("123", 1L));
    }

    @Test
    void aplicar_quandoJaCandidatado_lanca() {
        when(candidatoRepository.findById("123")).thenReturn(Optional.of(new Candidato()));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vagaValida()));
        when(candidaturaRepository.existsByCandidatoCpfAndVagaId("123", 1L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.aplicarParaVaga("123", 1L));
    }

    @Test
    void aplicar_quandoValido_salvaComStatusAberta() {
        when(candidatoRepository.findById("123")).thenReturn(Optional.of(new Candidato()));
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vagaValida()));
        when(candidaturaRepository.existsByCandidatoCpfAndVagaId("123", 1L)).thenReturn(false);
        when(candidaturaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Candidatura c = service.aplicarParaVaga("123", 1L);
        assertEquals(StatusCandidatura.ABERTA, c.getStatus());
    }

    @Test
    void listarMinhasCandidaturas_delega() {
        when(candidaturaRepository.findByCandidatoCpf("123")).thenReturn(List.of(new Candidatura()));
        assertEquals(1, service.listarMinhasCandidaturas("123").size());
    }

    @Test
    void listarCandidatosDaVaga_delega() {
        when(candidaturaRepository.findByVagaId(1L)).thenReturn(List.of(new Candidatura()));
        assertEquals(1, service.listarCandidatosDaVaga(1L).size());
    }

    @Test
    void atualizarStatus_quandoJaAprovada_lanca() {
        Candidatura c = new Candidatura();
        c.setStatus(StatusCandidatura.APROVADA);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(c));
        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(1L, StatusCandidatura.RECUSADA));
    }

    @Test
    void atualizarStatus_quandoValido_atualiza() {
        Candidatura c = new Candidatura();
        c.setStatus(StatusCandidatura.ABERTA);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(candidaturaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals(StatusCandidatura.RECUSADA, service.atualizarStatus(1L, StatusCandidatura.RECUSADA).getStatus());
    }

    @Test
    void aprovarEContratar_quandoJaAprovada_lanca() {
        Candidatura c = new Candidatura();
        c.setStatus(StatusCandidatura.APROVADA);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(c));
        assertThrows(IllegalStateException.class, () -> service.aprovarEContratar(1L, dadosContratacao()));
    }

    @Test
    void aprovarEContratar_quandoValido_aprovaEContrata() {
        Candidato candidato = new Candidato();
        candidato.setCpf("123");
        Candidatura c = new Candidatura();
        c.setStatus(StatusCandidatura.ABERTA);
        c.setCandidato(candidato);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(c));
        when(candidaturaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Candidatura resultado = service.aprovarEContratar(1L, dadosContratacao());
        assertEquals(StatusCandidatura.APROVADA, resultado.getStatus());
        verify(funcionarioService).contratarCandidato(eq("123"), any());
    }

    @Test
    void cancelar_quandoNaoEncontrada_lanca() {
        when(candidaturaRepository.findByVagaIdAndCandidatoCpf(1L, "123")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> service.cancelarCandidatura(1L, "123"));
    }

    @Test
    void cancelar_quandoExiste_deleta() {
        Candidatura c = new Candidatura();
        when(candidaturaRepository.findByVagaIdAndCandidatoCpf(1L, "123")).thenReturn(Optional.of(c));
        service.cancelarCandidatura(1L, "123");
        verify(candidaturaRepository).delete(c);
    }

    private DadosContratacaoDTO dadosContratacao() {
        return new DadosContratacaoDTO(new BigDecimal("3000"), LocalDate.now(), 1L, 2L, 40.0,
                "0001", "12345", "Banco", null, null, null);
    }
}
