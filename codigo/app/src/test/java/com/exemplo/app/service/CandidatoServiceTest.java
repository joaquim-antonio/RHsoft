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

import com.exemplo.app.dto.CandidatoProfileDTO;
import com.exemplo.app.dto.RegisterCandidatoDTO;
import com.exemplo.app.exception.CpfAlreadyExistsException;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.repository.CandidatoRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CandidatoServiceTest {

    @Mock
    CandidatoRepository candidatoRepository;
    @Mock
    PessoaRepository pessoaRepository;

    @InjectMocks
    CandidatoService service;

    @Test
    void listarTodos_retornaLista() {
        when(candidatoRepository.findAll()).thenReturn(List.of(new Candidato()));
        assertEquals(1, service.listarTodosOsCandidatos().size());
    }

    @Test
    void buscarPorCpf_quandoNaoExiste_retornaVazio() {
        when(candidatoRepository.findById("123")).thenReturn(Optional.empty());
        assertTrue(service.buscarCandidatoPorCpf("123").isEmpty());
    }

    @Test
    void salvarCandidato_delega() {
        Candidato c = new Candidato();
        when(candidatoRepository.save(c)).thenReturn(c);
        assertSame(c, service.salvarCandidato(c));
    }

    @Test
    void buscarPerfil_quandoNaoExiste_lanca() {
        when(candidatoRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarPerfilPorCpf("123"));
    }

    @Test
    void buscarPerfil_quandoExiste_mapeiaDto() {
        Candidato c = new Candidato();
        c.setCpf("123");
        c.setNome("Ana");
        c.setHabilidades(List.of("Java"));
        when(candidatoRepository.findById("123")).thenReturn(Optional.of(c));

        CandidatoProfileDTO dto = service.buscarPerfilPorCpf("123");
        assertEquals("Ana", dto.nome());
        assertEquals(List.of("Java"), dto.habilidades());
    }

    @Test
    void atualizarPerfil_quandoNaoEncontrado_lanca() {
        CandidatoProfileDTO dto = new CandidatoProfileDTO("123", "N", null, null, null, null, null);
        when(candidatoRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.atualizarPerfil("123", dto));
    }

    @Test
    void atualizarPerfil_quandoExiste_atualizaListas() {
        Candidato c = new Candidato();
        CandidatoProfileDTO dto = new CandidatoProfileDTO("123", "Novo", null, null,
                List.of("Python"), List.of("ADS"), null);
        when(candidatoRepository.findById("123")).thenReturn(Optional.of(c));
        when(candidatoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Candidato atualizado = service.atualizarPerfil("123", dto);
        assertEquals(List.of("Python"), atualizado.getHabilidades());
        assertEquals(List.of("ADS"), atualizado.getFormacao());
    }

    @Test
    void register_quandoCpfExiste_lanca() {
        RegisterCandidatoDTO dto = new RegisterCandidatoDTO("123", "senha", "Ana", "S", "999", "FEMININO",
                LocalDate.now(), new Endereco());
        when(pessoaRepository.existsById("123")).thenReturn(true);
        assertThrows(CpfAlreadyExistsException.class, () -> service.register(dto));
    }

    @Test
    void register_quandoValido_salva() {
        RegisterCandidatoDTO dto = new RegisterCandidatoDTO("123", "senha", "Ana", "S", "999", "FEMININO",
                LocalDate.now(), new Endereco());
        when(pessoaRepository.existsById("123")).thenReturn(false);
        when(candidatoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Candidato salvo = service.register(dto);
        assertEquals("Ana", salvo.getNome());
        assertNotNull(salvo.getUsuario());
    }

    @Test
    void atualizarCandidato_quandoNaoEncontrado_lanca() {
        when(candidatoRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.atualizarCandidato("123", new Candidato()));
    }

    @Test
    void atualizarCandidato_quandoExiste_atualiza() {
        Candidato existente = new Candidato();
        Candidato novos = new Candidato();
        novos.setHabilidades(List.of("Go"));
        when(candidatoRepository.findById("123")).thenReturn(Optional.of(existente));
        when(candidatoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Candidato atualizado = service.atualizarCandidato("123", novos);
        assertEquals(List.of("Go"), atualizado.getHabilidades());
    }
}
