package com.exemplo.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.exemplo.app.dto.DadosContratacaoDTO;
import com.exemplo.app.dto.RegisterFuncionarioDTO;
import com.exemplo.app.exception.CpfAlreadyExistsException;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Pessoa;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.transaction.Transactional;

@Service
public class FuncionarioService {

    @Autowired
    FuncionarioRepository repository;

    @Autowired
    PessoaRepository pessoaRepository;

    @Autowired
    DepartamentoService deptoService;

    @Autowired
    CargoService cargoService;

    @Autowired
    DepartamentoService departamentoService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public List<Funcionario> listarTodosFuncionarios() {
        List<Funcionario> funcionarios = new ArrayList<>();
        funcionarioRepository.findAll().forEach(funcionarios::add);
        return funcionarios;
    }

    /**
     * Registra um NOVO funcionário
     */
    @Transactional
    public Funcionario register(RegisterFuncionarioDTO body) {
        // Validação
        if (pessoaRepository.existsById(body.cpf())) {
            throw new CpfAlreadyExistsException("CPF já cadastrado");
        }

        // Funcionario
        Funcionario newFuncionario = new Funcionario();
        newFuncionario.setCpf(body.cpf());
        newFuncionario.setNome(body.nome());
        newFuncionario.setSobrenome(body.sobrenome());
        newFuncionario.setTelefone(body.telefone());
        newFuncionario.setSexo(TipoGenero.valueOf(body.sexo()));
        newFuncionario.setDataNascimento(body.dataNascimento());
        newFuncionario.setSalario(body.salario());
        newFuncionario.setDataAdmissao(body.dataAdmissao());
        newFuncionario.setHorasTrabalhadas(body.horasTrabalhadas());
        newFuncionario.setHorasExtras(0.00);

        // Endereco
        Endereco newEndereco = new Endereco();
        newEndereco.setNumero(body.endereco().getNumero());
        newEndereco.setRua(body.endereco().getRua());
        newEndereco.setComplemento(body.endereco().getComplemento());
        newEndereco.setBairro(body.endereco().getBairro());
        newEndereco.setLogradouro(body.endereco().getLogradouro());
        newEndereco.setCep(body.endereco().getCep());
        newEndereco.setCidade(body.endereco().getCidade());
        newEndereco.setEstado(body.endereco().getEstado());
        newFuncionario.setEndereco(newEndereco);

        // criando usuario
        Usuario newUser = new Usuario();
        newUser.setPasswordHash(new BCryptPasswordEncoder().encode(body.password()));
        newUser.setStatus(true);
        newFuncionario.setUsuario(newUser);
        newUser.setPessoa(newFuncionario);

        // criando conta bancaria
        ContaBancaria newConta = new ContaBancaria(
                body.contaBancaria().getAgencia(),
                body.contaBancaria().getNumero(),
                body.contaBancaria().getNomeBanco(),
                body.contaBancaria().getChavePix());
        newFuncionario.setContaBancaria(newConta);
        newConta.setFuncionario(newFuncionario);

        // criando cargo
        newFuncionario.setCargo(cargoService.registrarCargo(body.cargo()));

        // adicionar departamento
        newFuncionario.setDepartamento(deptoService.registrarDepartamento(body.departamento()));

        // JPA salva no db
        return this.repository.save(newFuncionario);
    }

    /**
     * Converte um Candidato existente em um Funcionario.
     */
    @Transactional
    public Funcionario contratarCandidato(String cpf, DadosContratacaoDTO dados) {
        Pessoa pessoa = pessoaRepository.findById(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Candidato não encontrado com CPF: " + cpf));

        if (!(pessoa instanceof Candidato)) {
            throw new IllegalStateException("Esta pessoa não é um Candidato.");
        }

        // Preparar o Funcionario
        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setCpf(pessoa.getCpf());
        novoFuncionario.setNome(pessoa.getNome());
        novoFuncionario.setSobrenome(pessoa.getSobrenome());
        novoFuncionario.setTelefone(pessoa.getTelefone());
        novoFuncionario.setSexo(pessoa.getSexo());
        novoFuncionario.setDataNascimento(pessoa.getDataNascimento());
        novoFuncionario.setEndereco(pessoa.getEndereco());
        novoFuncionario.setUsuario(pessoa.getUsuario());

        // 3. Dados Contratuais
        novoFuncionario.setSalario(dados.salario());
        novoFuncionario.setDataAdmissao(dados.dataAdmissao());
        novoFuncionario.setHorasTrabalhadas(dados.horasTrabalhadas());
        novoFuncionario.setHorasExtras(0.00);
        novoFuncionario.setTipoAcrescimo(dados.tipoAcrescimo());
        novoFuncionario.setTipoInsalubridade(dados.tipoInsalubridade());

        Cargo cargo = cargoService.buscarCargoPorCodigo(dados.cargoId());
        Departamento departamento = departamentoService.buscarDepartamentoPorCodigo(dados.departamentoId());

        novoFuncionario.setCargo(cargo);
        novoFuncionario.setDepartamento(departamento);

        // Conta Bancária
        ContaBancaria novaConta = new ContaBancaria(
                dados.agencia(),
                dados.numeroConta(),
                dados.nomeBanco(),
                dados.chavePix());
        novoFuncionario.setContaBancaria(novaConta);
        novaConta.setFuncionario(novoFuncionario);

        // Troca de Tipo
        pessoaRepository.delete(pessoa);
        pessoaRepository.flush();

        return funcionarioRepository.save(novoFuncionario);
    }
}