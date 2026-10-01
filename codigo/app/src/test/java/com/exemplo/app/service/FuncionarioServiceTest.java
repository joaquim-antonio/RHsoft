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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.exemplo.app.dto.DadosContratacaoDTO;
import com.exemplo.app.dto.RegisterFuncionarioDTO;
import com.exemplo.app.exception.CpfAlreadyExistsException;
import com.exemplo.app.exception.RegraNegocioException;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    FuncionarioRepository funcionarioRepository;
    @Mock
    PessoaRepository pessoaRepository;
    @Mock
    CargoRepository cargoRepository;
    @Mock
    DepartamentoRepository departamentoRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EntityManager entityManager;

    @InjectMocks
    FuncionarioService service;

    private RegisterFuncionarioDTO dtoValido(Long cargoId, Long deptoId) {
        Cargo cargo = new Cargo();
        Departamento depto = new Departamento();
        try {
            java.lang.reflect.Field f = Cargo.class.getDeclaredField("codigo");
            f.setAccessible(true);
            f.set(cargo, cargoId);
            java.lang.reflect.Field g = Departamento.class.getDeclaredField("codigo");
            g.setAccessible(true);
            g.set(depto, deptoId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new RegisterFuncionarioDTO("123", "senha", "Ana", "Silva", "999", "FEMININO",
                LocalDate.of(1990, 1, 1), new Endereco(), new BigDecimal("3000"), LocalDate.now(),
                40.0, cargo, null, null, depto, null, null);
    }

    @Test
    void listarTodos_retornaLista() {
        when(funcionarioRepository.findAll()).thenReturn(List.of(new Funcionario()));
        assertEquals(1, service.listarTodosFuncionarios().size());
    }

    @Test
    void buscarPorCpf_quandoNaoExiste_lanca() {
        when(funcionarioRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarPorCpf("123"));
    }

    @Test
    void register_quandoCpfExiste_lanca() {
        when(pessoaRepository.existsByCpf("123")).thenReturn(true);
        assertThrows(CpfAlreadyExistsException.class, () -> service.register(dtoValido(1L, 1L)));
    }

    @Test
    void register_quandoSalarioNulo_lanca() {
        Cargo cargo = cargoComId(1L);
        Departamento depto = new Departamento(1L, null, null);
        RegisterFuncionarioDTO dto = new RegisterFuncionarioDTO("123", "senha", "Ana", "Silva", "999", "FEMININO",
                LocalDate.of(1990, 1, 1), null, null, LocalDate.now(), 40.0, cargo, null, null,
                depto, null, null);
        when(pessoaRepository.existsByCpf("123")).thenReturn(false);
        assertThrows(RegraNegocioException.class, () -> service.register(dto));
    }

    private Cargo cargoComId(Long id) {
        Cargo c = new Cargo();
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
    void register_quandoCargoNaoExiste_lanca() {
        when(pessoaRepository.existsByCpf("123")).thenReturn(false);
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RegraNegocioException.class, () -> service.register(dtoValido(1L, 1L)));
    }

    @Test
    void register_quandoDepartamentoNaoExiste_lanca() {
        when(pessoaRepository.existsByCpf("123")).thenReturn(false);
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(new Cargo()));
        when(departamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RegraNegocioException.class, () -> service.register(dtoValido(1L, 1L)));
    }

    @Test
    void register_quandoValido_salva() {
        when(pessoaRepository.existsByCpf("123")).thenReturn(false);
        when(cargoRepository.findById(1L)).thenReturn(Optional.of(new Cargo()));
        when(departamentoRepository.findById(1L)).thenReturn(Optional.of(new Departamento()));
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(funcionarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.register(dtoValido(1L, 1L));
        verify(funcionarioRepository).save(any(Funcionario.class));
    }

    @Test
    void contratarCandidato_quandoCargoNaoExiste_lanca() {
        when(cargoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.contratarCandidato("123", dadosContratacao()));
    }

    @Test
    void contratarCandidato_quandoValido_promoveESalva() {
        Cargo cargo = new Cargo();
        Departamento depto = new Departamento();
        Funcionario func = new Funcionario();

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(departamentoRepository.findById(2L)).thenReturn(Optional.of(depto));
        when(funcionarioRepository.findById("123")).thenReturn(Optional.of(func));
        when(funcionarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.contratarCandidato("123", dadosContratacao());

        verify(pessoaRepository).promoverCandidatoParaFuncionario("123");
        verify(funcionarioRepository).save(func);
        assertSame(cargo, func.getCargo());
    }

    private DadosContratacaoDTO dadosContratacao() {
        return new DadosContratacaoDTO(new BigDecimal("3000"), LocalDate.now(), 1L, 2L, 40.0,
                "0001", "12345", "Banco", null, null, null);
    }
}
