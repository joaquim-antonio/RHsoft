package com.exemplo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

/**
 * Service para gerenciar a lógica de negócio de Pagamentos.
 */
@Service
public class PagamentoService {

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    /**
     * Busca um pagamento pelo seu código único.
     * @param codigo O código do pagamento.
     * @return O Pagamento encontrado.
     * @throws EntityNotFoundException se o pagamento não for encontrado.
     */
    public Pagamento buscarPagamentoPorCodigo(String codigo) {
        return pagamentoRepository.findById(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento com código " + codigo + " não encontrado."));
    }

    /**
     * Lista todos os pagamentos associados a um funcionário específico.
     * @param cpf O CPF do funcionário.
     * @return Uma lista de Pagamentos.
     * @throws EntityNotFoundException se o funcionário não for encontrado.
     */
    public List<Pagamento> listarPagamentosPorFuncionario(String cpf) {
        if (!funcionarioRepository.existsById(cpf)) {
            throw new EntityNotFoundException("Funcionário com CPF " + cpf + " inexistente.");
        }
        return pagamentoRepository.findByFuncionarioCpf(cpf);
    }

    /**
     * Cria um novo pagamento no sistema.
     * @param pagamento O objeto de Pagamento a ser criado.
     * @param cpf O CPF do funcionário a ser associado.
     * @param idFolha O ID da folha de pagamento a ser associada.
     * @return O Pagamento salvo.
     * @throws EntityNotFoundException se o funcionário ou a folha de pagamento não forem encontrados.
     */
    @Transactional
    public Pagamento criarPagamento(Pagamento pagamento, String cpf, Long idFolha) {
        Funcionario funcionario = funcionarioRepository.findById(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário com CPF " + cpf + " não encontrado para associar ao pagamento."));
        FolhaPagamento folhaPagamento = folhaPagamentoRepository.findById(idFolha)
                .orElseThrow(() -> new EntityNotFoundException("Folha de Pagamento com ID " + idFolha + " não encontrada."));

        pagamento.setFuncionario(funcionario);
        pagamento.setFolhaPagamento(folhaPagamento);

        // Garante que os itens de pagamento tenham a referência correta ao pagamento pai
        if (pagamento.getItens() != null) {
            pagamento.getItens().forEach(item -> item.setPagamento(pagamento));
        }

        pagamento.calcularTotais();

        return pagamentoRepository.save(pagamento);
    }

    /**
     * Deleta um pagamento do sistema.
     * @param codigo O código do pagamento a ser deletado.
     * @throws EntityNotFoundException se o pagamento não for encontrado.
     */
    @Transactional
    public void deletarPagamento(String codigo) {
        if (!pagamentoRepository.existsById(codigo)) {
            throw new EntityNotFoundException("Pagamento com código " + codigo + " inexistente para exclusão.");
        }
        pagamentoRepository.deleteById(codigo);
    }

    /**
     * Recalcula os totais (proventos, descontos e valor líquido) de um pagamento existente.
     * @param codigo O código do pagamento a ser recalculado.
     * @return O Pagamento com os totais atualizados.
     * @throws EntityNotFoundException se o pagamento não for encontrado.
     */
    @Transactional
    public Pagamento recalcularTotais(String codigo) {
        Pagamento pagamento = buscarPagamentoPorCodigo(codigo);
        pagamento.calcularTotais();
        return pagamentoRepository.save(pagamento);
    }
}
