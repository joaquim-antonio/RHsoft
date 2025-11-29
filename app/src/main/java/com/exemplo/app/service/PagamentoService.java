package com.exemplo.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

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

    @Autowired
    private CalculadoraFolhaService calculadoraService;

    @Autowired
    private ConfiguracaoService configuracaoService;

    /**
     * Busca um pagamento pelo seu código único.
     * * @param codigo O código do pagamento.
     * @return O Pagamento encontrado.
     * @throws EntityNotFoundException se o pagamento não for encontrado.
     */
    public Pagamento buscarPagamentoPorCodigo(String codigo) {
        return pagamentoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento com código " + codigo + " não encontrado."));
    }

    /**
     * Lista todos os pagamentos associados a um funcionário específico.
     * * @param cpf O CPF do funcionário.
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
     * Utiliza a Calculadora Central para garantir consistência fiscal.
     * * @param pagamentoInput O objeto de Pagamento com dados iniciais ou itens manuais extras.
     * @param cpf            O CPF do funcionário a ser associado.
     * @param idFolha        O ID da folha de pagamento a ser associada.
     * @return O Pagamento salvo.
     */
    @Transactional
    public Pagamento criarPagamento(Pagamento pagamentoInput, String cpf, Long idFolha) {
        Funcionario funcionario = funcionarioRepository.findById(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado"));

        FolhaPagamento folha = folhaPagamentoRepository.findById(idFolha)
                .orElseThrow(() -> new EntityNotFoundException("Folha não encontrada"));

        if (folha.getStatus() != StatusPagamento.ABERTO) {
            throw new IllegalStateException("Não é possível criar pagamentos em uma folha que não está ABERTA.");
        }

        // Se o objeto input vier nulo, cria um novo
        Pagamento novoPagamento = pagamentoInput != null ? pagamentoInput : new Pagamento();
        
        novoPagamento.setFuncionario(funcionario);
        novoPagamento.setFolhaPagamento(folha);
        
        // Garante dados de cabeçalho se não vierem preenchidos
        if (novoPagamento.getMesAnoReferencia() == null) {
            novoPagamento.setMesAnoReferencia(LocalDate.now().toString().substring(0, 7));
        }
        if (novoPagamento.getVencimento() == null) {
            novoPagamento.setVencimento(LocalDate.now().plusDays(5));
        }

        // Gera código único se não existir
        if (novoPagamento.getCodigo() == null || novoPagamento.getCodigo().isEmpty()) {
            String sufixoCpf = cpf.length() >= 3 ? cpf.substring(0, 3) : "000";
            novoPagamento.setCodigo("PAY-" + folha.getId() + "-" + System.currentTimeMillis() + "-" + sufixoCpf);
        }

        // Aplica o motor de cálculo padrão (Salário, INSS, IRRF, VT)
        ConfiguracaoSistema config = configuracaoService.buscarConfiguracaoAtual();
        calculadoraService.processarFolhaFuncionario(novoPagamento, funcionario, config);

        // Se houver itens manuais
        if (pagamentoInput != null && pagamentoInput.getItens() != null && !pagamentoInput.getItens().isEmpty()) {
            List<ItemPagamento> itensManuais = new ArrayList<>();
            
            // Filtra para pegar apenas os que foram passados manualmente no input
            for (ItemPagamento item : pagamentoInput.getItens()) {
                item.setPagamento(novoPagamento);
                itensManuais.add(item);
            }
            
            // Adiciona à lista já populada pela calculadora
            novoPagamento.getItens().addAll(itensManuais);
            
            // Recalcula os totais (Proventos - Descontos)
            novoPagamento.calcularTotais();
        }

        return pagamentoRepository.save(novoPagamento);
    }

    /**
     * Deleta um pagamento do sistema.
     * * @param codigo O código do pagamento a ser deletado.
     * @throws EntityNotFoundException se o pagamento não for encontrado.
     */
    @Transactional
    public void deletarPagamento(String codigo) {
        Pagamento pagamento = buscarPagamentoPorCodigo(codigo);
        
        if (pagamento.getFolhaPagamento().getStatus() != StatusPagamento.ABERTO) {
            throw new IllegalStateException("Não é possível deletar pagamentos de uma folha fechada/consolidada.");
        }
        
        pagamentoRepository.delete(pagamento);
    }

    /**
     * Recalcula os totais (proventos, descontos e valor líquido) de um pagamento
     * existente.
     * * @param codigo O código do pagamento a ser recalculado.
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