package com.exemplo.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.ContaBancariaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ContaBancariaService {

    @Autowired
    private ContaBancariaRepository contaBancariaRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // GET
    public ContaBancaria buscarContaPorFuncionarioCPF(String cpf) {
        Funcionario funcionario = funcionarioRepository.findById(cpf)
            .orElseThrow(() -> new EntityNotFoundException("Funcionário não cadastrado."));

        if (funcionario.getContaBancaria() == null) {
            throw new EntityNotFoundException("Nenhuma conta bancária encontrada para este funcionário.");
        }

        return funcionario.getContaBancaria();
    }

    // POST
    public ContaBancaria adicionarConta(String cpf, ContaBancaria conta) {
        Funcionario funcionario = funcionarioRepository.findById(cpf)
            .orElseThrow(() -> new EntityNotFoundException("Funcionário não cadastrado."));

        if (funcionario.getContaBancaria() != null) {
            throw new IllegalStateException("Este funcionário já possui uma conta bancária. Para alterá-la, use o método de substituição.");
        }

        conta.setFuncionario(funcionario);
        funcionario.setContaBancaria(conta);

        return contaBancariaRepository.save(conta);
    }

    // PUT - substitui toda a conta
    public ContaBancaria substituirConta(String cpf, ContaBancaria contaSubstituta) {
        Funcionario funcionario = funcionarioRepository.findById(cpf)
            .orElseThrow(() -> new EntityNotFoundException("Funcionário com CPF " + cpf + " não encontrado."));

        ContaBancaria contaAntiga = funcionario.getContaBancaria();

        if (contaAntiga == null) {
            throw new IllegalStateException("Este funcionário não possui uma conta bancária para ser substituída. Use o método de adição primeiro.");
        }

        
        funcionario.setContaBancaria(null);
        contaBancariaRepository.delete(contaAntiga);

        contaSubstituta.setFuncionario(funcionario);
        funcionario.setContaBancaria(contaSubstituta);

        return contaBancariaRepository.save(contaSubstituta);
    }

    // PATCH 
    public ContaBancaria atualizarChavePix(String cpf, String novaChave) {
        ContaBancaria conta = buscarContaPorFuncionarioCPF(cpf);

        conta.atualizarChavePix(novaChave);

        return contaBancariaRepository.save(conta);
    }
}
