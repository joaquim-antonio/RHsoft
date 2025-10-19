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

@Service
public class PagamentoService {

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    //GET
    public Pagamento buscarPagamentoPorCodigo(String codigo){
        return pagamentoRepository.findById(codigo)
            .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));
    }

    public List<Pagamento> listarPagamentosPorFuncionario(String cpf) {
        if (!funcionarioRepository.existsById(cpf)) {
            throw new EntityNotFoundException("Funcionário inexistente");
        }
        return pagamentoRepository.findByFuncionarioCpf(cpf);
    }

    //POST
    public Pagamento criarPagamento(Pagamento pagamento, String cpf, Long idFolha){
        Funcionario funcionario = funcionarioRepository.findById(cpf)
            .orElseThrow(() -> new EntityNotFoundException("Funcionario não encontrado"));
        FolhaPagamento folhaPagamento = folhaPagamentoRepository.findById(idFolha)
            .orElseThrow(() -> new EntityNotFoundException("Folha não encontrada"));
        pagamento.setFuncionario(funcionario);
        pagamento.setFolhaPagamento(folhaPagamento);

        pagamento.calcularTotais();

        return pagamentoRepository.save(pagamento);
    }

    //DELETE
    public void deletarPagamento(String codigo){
        if (!pagamentoRepository.existsById(codigo)){
            throw new EntityNotFoundException("Pagamento inexistente");
        }
        pagamentoRepository.deleteById(codigo);
    }

    //PATCH
    public Pagamento recalcularTotais(String codigo){
        Pagamento pagamento = buscarPagamentoPorCodigo(codigo);
        pagamento.calcularTotais();
        return pagamentoRepository.save(pagamento);
    }

}
