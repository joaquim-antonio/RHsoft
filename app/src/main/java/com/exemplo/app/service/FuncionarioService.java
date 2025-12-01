package com.exemplo.app.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.dto.DadosContratacaoDTO;
import com.exemplo.app.dto.DepartamentoCountDTO;
import com.exemplo.app.dto.RegisterFuncionarioDTO;
import com.exemplo.app.exception.CpfAlreadyExistsException;
import com.exemplo.app.exception.RegraNegocioException;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public List<DepartamentoCountDTO> contarFuncionariosPorDepartamento() {
        return funcionarioRepository.findCountByDepartamento();
    }

    public List<Funcionario> listarTodosFuncionarios() {
        return (List<Funcionario>) funcionarioRepository.findAll();
    }

    public Funcionario buscarPorCpf(String cpf) {
        return funcionarioRepository.findById(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado"));
    }

    @Transactional
    public void register(RegisterFuncionarioDTO data) {
        if (pessoaRepository.existsByCpf(data.cpf())) {
            throw new CpfAlreadyExistsException("CPF já cadastrado.");
        }

        if (pessoaRepository.existsByCpf(data.cpf())) {
            throw new CpfAlreadyExistsException("O CPF " + data.cpf() + " já está cadastrado no sistema.");
        }

        // Salário Negativo ou Zero
        if (data.salario() == null || data.salario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O salário deve ser maior que zero. Valor informado: " + data.salario());
        }

        // Carga Horária Inválida
        if (data.horasTrabalhadas() == null || data.horasTrabalhadas() <= 0) {
            throw new RegraNegocioException("A carga horária deve ser positiva.");
        }

        // Cargo inexistente
        Cargo cargo = cargoRepository.findById(data.cargo().getCodigo())
                .orElseThrow(() -> new RegraNegocioException(
                        "O Cargo selecionado (ID " + data.cargo().getCodigo() + ") não existe no cadastro."));

        // Departamento inexistente
        Departamento departamento = departamentoRepository.findById(data.departamento().getCodigo())
                .orElseThrow(() -> new RegraNegocioException(
                        "O Departamento selecionado (ID " + data.departamento().getCodigo() + ") não existe."));

        Funcionario func = new Funcionario();
        func.setCpf(data.cpf());
        func.setNome(data.nome());
        func.setSobrenome(data.sobrenome());
        func.setTelefone(data.telefone());
        func.setSexo(TipoGenero.valueOf(data.sexo()));
        func.setDataNascimento(data.dataNascimento());
        func.setEndereco(data.endereco());

        func.setSalario(data.salario());
        func.setDataAdmissao(data.dataAdmissao());
        func.setHorasTrabalhadas(data.horasTrabalhadas());

        func.setCargo(cargo);
        func.setDepartamento(departamento);

        func.setTipoAcrescimo(data.tipoAcrescimo());
        func.setTipoInsalubridade(data.tipoInsalubridade());

        if (data.contaBancaria() != null) {
            data.contaBancaria().setFuncionario(func);
            func.setContaBancaria(data.contaBancaria());
        }

        Usuario usuario = new Usuario();
        usuario.setPasswordHash(passwordEncoder.encode(data.password()));
        usuario.setStatus(true);
        usuario.setPessoa(func);
        func.setUsuario(usuario);

        funcionarioRepository.save(func);
    }

    @Transactional
    public void contratarCandidato(String cpf, DadosContratacaoDTO dados) {
        Cargo cargo = cargoRepository.findById(dados.cargoId())
                .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado"));
        Departamento departamento = departamentoRepository.findById(dados.departamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        if (dados.salario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Salário de contratação inválido.");
        }

        pessoaRepository.promoverCandidatoParaFuncionario(cpf);

        entityManager.flush();
        entityManager.clear();

        Funcionario funcionario = funcionarioRepository.findById(cpf)
                .orElseThrow(() -> new IllegalStateException("Erro crítico: Falha na conversão de Pessoa."));

        funcionario.setSalario(dados.salario());
        funcionario.setDataAdmissao(dados.dataAdmissao());
        funcionario.setHorasTrabalhadas(dados.horasTrabalhadas());
        funcionario.setCargo(cargo);
        funcionario.setDepartamento(departamento);
        funcionario.setTipoAcrescimo(dados.tipoAcrescimo());
        funcionario.setTipoInsalubridade(dados.tipoInsalubridade());

        ContaBancaria conta = new ContaBancaria(
                dados.agencia(), dados.numeroConta(), dados.nomeBanco(), dados.chavePix());
        conta.setFuncionario(funcionario);
        funcionario.setContaBancaria(conta);

        funcionarioRepository.save(funcionario);
    }
}