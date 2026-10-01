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

import com.exemplo.app.model.Cargo;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private CargoService cargoService;

    private Cargo cargo() {
        return cargoComId(1L, "Desenvolvedor");
    }

    private Cargo cargoComId(Long id, String nome) {
        Cargo c = new Cargo();
        c.setNome(nome);
        try {
            java.lang.reflect.Field f = Cargo.class.getDeclaredField("codigo");
            f.setAccessible(true);
            f.set(c, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return c;
    }

    @Test
    void listarTodosCargos_retornaLista() {
        when(cargoRepository.findAll()).thenReturn(List.of(cargo()));
        assertEquals(1, cargoService.listarTodosCargos().size());
    }

    @Test
    void buscarCargoPorCodigo_quandoExiste_retorna() {
        when(cargoRepository.findByCodigo(1L)).thenReturn(Optional.of(cargo()));
        assertEquals("Desenvolvedor", cargoService.buscarCargoPorCodigo(1L).getNome());
    }

    @Test
    void buscarCargoPorCodigo_quandoNaoExiste_lancaExcecao() {
        when(cargoRepository.findByCodigo(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> cargoService.buscarCargoPorCodigo(1L));
    }

    @Test
    void criarCargo_quandoNomeJaExiste_lancaExcecao() {
        when(cargoRepository.findByNome("Desenvolvedor")).thenReturn(Optional.of(cargo()));
        assertThrows(IllegalArgumentException.class, () -> cargoService.criarCargo(cargo()));
    }

    @Test
    void criarCargo_quandoNovo_salva() {
        when(cargoRepository.findByNome("Desenvolvedor")).thenReturn(Optional.empty());
        when(cargoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals("Desenvolvedor", cargoService.criarCargo(cargo()).getNome());
    }

    @Test
    void registrarCargo_quandoExiste_retornaExistente() {
        Cargo existente = cargo();
        when(cargoRepository.findByNome("Desenvolvedor")).thenReturn(Optional.of(existente));
        assertSame(existente, cargoService.registrarCargo(cargo()));
    }

    @Test
    void registrarCargo_quandoNovo_salvaNovo() {
        when(cargoRepository.findByNome("Novo")).thenReturn(Optional.empty());
        when(cargoRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        Cargo c = new Cargo();
        c.setNome("Novo");
        assertEquals("Novo", cargoService.registrarCargo(c).getNome());
    }

    @Test
    void atualizarCargo_quandoNomeEmUsoPorOutro_lancaExcecao() {
        Cargo existente = cargo();
        Cargo outro = cargoComId(2L, "Gestor");
        when(cargoRepository.findByCodigo(1L)).thenReturn(Optional.of(existente));
        when(cargoRepository.findByNome("Gestor")).thenReturn(Optional.of(outro));

        Cargo atualizado = new Cargo();
        atualizado.setNome("Gestor");
        assertThrows(IllegalArgumentException.class, () -> cargoService.atualizarCargo(1L, atualizado));
    }

    @Test
    void atualizarCargo_comNomeDisponivel_atualiza() {
        Cargo existente = cargo();
        when(cargoRepository.findByCodigo(1L)).thenReturn(Optional.of(existente));
        when(cargoRepository.findByNome("Arquiteto")).thenReturn(Optional.empty());
        when(cargoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Cargo atualizado = new Cargo();
        atualizado.setNome("Arquiteto");
        assertEquals("Arquiteto", cargoService.atualizarCargo(1L, atualizado).getNome());
    }

    @Test
    void deletarCargo_quandoEmUso_lancaExcecao() {
        when(cargoRepository.findByCodigo(1L)).thenReturn(Optional.of(cargo()));
        when(funcionarioRepository.existsByCargoCodigo(1L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> cargoService.deletarCargo(1L));
    }

    @Test
    void deletarCargo_quandoLivre_deleta() {
        Cargo c = cargo();
        when(cargoRepository.findByCodigo(1L)).thenReturn(Optional.of(c));
        when(funcionarioRepository.existsByCargoCodigo(1L)).thenReturn(false);
        cargoService.deletarCargo(1L);
        verify(cargoRepository).delete(c);
    }
}
