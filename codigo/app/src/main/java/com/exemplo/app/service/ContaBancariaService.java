package com.exemplo.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.repository.ContaBancariaRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

/**
 * Service para gerenciar a lógica de negócio de Contas Bancárias.
 */
@Service
public class ContaBancariaService {

    @Autowired
    private ContaBancariaRepository contaBancariaRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    /**
     * Busca uma conta bancária pelo seu ID.
     * @param id O ID da conta bancária.
     * @return A ContaBancaria encontrada.
     * @throws EntityNotFoundException se a conta não for encontrada.
     */
    public ContaBancaria buscarContaBancariaPorId(Long id) {
        return contaBancariaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta Bancária com ID " + id + " não encontrada."));
    }

    /**
     * Busca a conta bancária associada a um funcionário pelo CPF.
     * @param cpf O CPF do funcionário.
     * @return A ContaBancaria do funcionário.
     * @throws EntityNotFoundException se o funcionário ou a conta não forem encontrados.
     */
    public ContaBancaria buscarContaBancariaPorFuncionario(String cpf) {
        Funcionario funcionario = funcionarioRepository.findById(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário com CPF " + cpf + " não encontrado."));

        ContaBancaria conta = contaBancariaRepository.findByFuncionario(funcionario)
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma Conta Bancária encontrada para o funcionário com CPF " + cpf + "."));
        
        return conta;
    }

    /**
     * Cria uma nova conta bancária e a associa a um funcionário.
     * @param conta O objeto ContaBancaria a ser criado.
     * @param cpf O CPF do funcionário a ser associado.
     * @return A ContaBancaria salva.
     * @throws EntityNotFoundException se o funcionário não for encontrado.
     * @throws IllegalStateException se o funcionário já possuir uma conta bancária.
     */
    @Transactional
    public ContaBancaria criarContaBancaria(ContaBancaria conta, String cpf) {
        Funcionario funcionario = funcionarioRepository.findById(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário com CPF " + cpf + " não encontrado."));

        if (contaBancariaRepository.findByFuncionario(funcionario).isPresent()) {
            throw new IllegalStateException("O funcionário com CPF " + cpf + " já possui uma conta bancária cadastrada.");
        }

        conta.setFuncionario(funcionario);
        return contaBancariaRepository.save(conta);
    }

    /**
     * Atualiza todos os dados de uma conta bancária existente.
     * @param id O ID da conta a ser atualizada.
     * @param agencia A nova agência.
     * @param numero O novo número da conta.
     * @param nomeBanco O novo nome do banco.
     * @param chavePix A nova chave Pix.
     * @param funcionarioCpf O CPF do funcionário (para validação, se necessário).
     * @return A ContaBancaria atualizada.
     * @throws EntityNotFoundException se a conta não for encontrada.
     */
    @Transactional
    public ContaBancaria atualizarContaBancaria(Long id, String agencia, String numero, String nomeBanco, String chavePix, String funcionarioCpf) {
        ContaBancaria conta = buscarContaBancariaPorId(id);
        
        // Validação adicional: garante que a conta pertence ao funcionário correto, se o CPF for fornecido
        if (funcionarioCpf != null && !conta.getFuncionario().getCpf().equals(funcionarioCpf)) {
             throw new IllegalStateException("A conta bancária não pertence ao funcionário com CPF " + funcionarioCpf + ".");
        }

        conta.setAgencia(agencia);
        conta.setNumero(numero);
        conta.setNomeBanco(nomeBanco);
        conta.setChavePix(chavePix);
        
        return contaBancariaRepository.save(conta);
    }

    /**
     * Atualiza apenas a chave Pix de uma conta bancária.
     * @param id O ID da conta a ser atualizada.
     * @param novaChavePix A nova chave Pix.
     * @return A ContaBancaria com a chave Pix atualizada.
     * @throws EntityNotFoundException se a conta não for encontrada.
     */
    @Transactional
    public ContaBancaria atualizarChavePix(Long id, String novaChavePix) {
        ContaBancaria conta = buscarContaBancariaPorId(id);
        conta.setChavePix(novaChavePix);
        return contaBancariaRepository.save(conta);
    }

    /**
     * Deleta uma conta bancária do sistema.
     * @param id O ID da conta bancária a ser deletada.
     * @throws EntityNotFoundException se a conta não for encontrada.
     */
    @Transactional
    public void deletarContaBancaria(Long id) {
        if (!contaBancariaRepository.existsById(id)) {
            throw new EntityNotFoundException("Conta Bancária com ID " + id + " inexistente para exclusão.");
        }
        contaBancariaRepository.deleteById(id);
    }
}
