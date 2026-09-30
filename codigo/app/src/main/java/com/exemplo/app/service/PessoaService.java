package com.exemplo.app.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.exception.RegraNegocioException;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Pessoa;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.PessoaRepository;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;
    
    @Autowired
    private CargoRepository cargoRepository;
    
    @Autowired
    private DepartamentoRepository departamentoRepository;

    public List<Pessoa> listarTodasPessoas(){
        return pessoaRepository.findAll();
    }

    public Optional<Pessoa> buscarPessoaCPF(String cpf){
        return pessoaRepository.findById(cpf);
    }

    public Pessoa salvarPessoa(Pessoa pessoa){
        return pessoaRepository.save(pessoa);
    }

    public void excluirPessoa(String cpf){
        if (!pessoaRepository.existsById(cpf)) {
            throw new RegraNegocioException("Pessoa não encontrada para exclusão.");
        }
        pessoaRepository.deleteById(cpf);
    }

    @Transactional
    public Pessoa atualizarPessoa(String cpf, Pessoa dadosAtualizados){

        Pessoa pessoaExistente = pessoaRepository.findById(cpf)
                .orElseThrow(() -> new RegraNegocioException("Pessoa não encontrada com CPF: " + cpf));

        // Atualiza Dados Básicos
        if (dadosAtualizados.getNome() != null) pessoaExistente.setNome(dadosAtualizados.getNome());
        if (dadosAtualizados.getSobrenome() != null) pessoaExistente.setSobrenome(dadosAtualizados.getSobrenome());
        if (dadosAtualizados.getTelefone() != null) pessoaExistente.setTelefone(dadosAtualizados.getTelefone());
        if (dadosAtualizados.getDataNascimento() != null) pessoaExistente.setDataNascimento(dadosAtualizados.getDataNascimento());
        if (dadosAtualizados.getSexo() != null) pessoaExistente.setSexo(dadosAtualizados.getSexo());

        // Atualiza Endereço
        if (dadosAtualizados.getEndereco() != null) {
            if (pessoaExistente.getEndereco() == null) {
                pessoaExistente.setEndereco(dadosAtualizados.getEndereco());
            } else {
                var endExistente = pessoaExistente.getEndereco();
                var endNovo = dadosAtualizados.getEndereco();
                endExistente.setCep(endNovo.getCep());
                endExistente.setRua(endNovo.getRua());
                endExistente.setNumero(endNovo.getNumero());
                endExistente.setBairro(endNovo.getBairro());
                endExistente.setCidade(endNovo.getCidade());
                endExistente.setEstado(endNovo.getEstado());
                endExistente.setLogradouro(endNovo.getLogradouro());
            }
        }

        // ATUALIZAÇÃO DE FUNCIONÁRIO
        if (pessoaExistente instanceof Funcionario funcExistente && dadosAtualizados instanceof Funcionario funcNovosDados) {
            
            // Salário
            if (funcNovosDados.getSalario() != null) {
                if (funcNovosDados.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RegraNegocioException("Erro na Edição: O salário não pode ser zero ou negativo.");
                }
                funcExistente.setSalario(funcNovosDados.getSalario());
            }

            // Cargo
            if (funcNovosDados.getCargo() != null) {
                Long cargoId = funcNovosDados.getCargo().getCodigo();
                if (cargoId != null) {
                    Cargo novoCargo = cargoRepository.findById(cargoId)
                        .orElseThrow(() -> new RegraNegocioException("Erro na Edição: Cargo com ID " + cargoId + " não existe."));
                    funcExistente.setCargo(novoCargo);
                }
            }

            // Departamento
            if (funcNovosDados.getDepartamento() != null) {
                Long deptoId = funcNovosDados.getDepartamento().getCodigo();
                if (deptoId != null) {
                    Departamento novoDepto = departamentoRepository.findById(deptoId)
                        .orElseThrow(() -> new RegraNegocioException("Erro na Edição: Departamento com ID " + deptoId + " não existe."));
                    funcExistente.setDepartamento(novoDepto);
                }
            }

            // Outros campos simples
            if (funcNovosDados.getDataAdmissao() != null) funcExistente.setDataAdmissao(funcNovosDados.getDataAdmissao());
            if (funcNovosDados.getHorasTrabalhadas() != null) funcExistente.setHorasTrabalhadas(funcNovosDados.getHorasTrabalhadas());
            
            funcExistente.setTipoAcrescimo(funcNovosDados.getTipoAcrescimo());
            funcExistente.setTipoInsalubridade(funcNovosDados.getTipoInsalubridade());

            // Banco
            if (funcNovosDados.getContaBancaria() != null) {
                if (funcExistente.getContaBancaria() == null) {
                    funcExistente.setContaBancaria(funcNovosDados.getContaBancaria());
                    funcExistente.getContaBancaria().setFuncionario(funcExistente);
                } else {
                    var contaEx = funcExistente.getContaBancaria();
                    var contaNova = funcNovosDados.getContaBancaria();
                    contaEx.setNomeBanco(contaNova.getNomeBanco());
                    contaEx.setAgencia(contaNova.getAgencia());
                    contaEx.setNumero(contaNova.getNumero());
                    contaEx.setChavePix(contaNova.getChavePix());
                }
            }
        }

        return pessoaRepository.save(pessoaExistente);
    }
}