package com.exemplo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.repository.AdministradorRepository;
import com.exemplo.app.repository.CandidatoRepository;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AdministradorService {
    @Autowired
    private final AdministradorRepository administradorRepository;

    @Autowired
    private final FuncionarioRepository funcionarioRepository;

    @Autowired
    private final CandidatoRepository candidatoRepository;

    @Autowired
    private final  FolhaPagamentoRepository folhaPagamentoRepository;

    public Administrador buscarPorId(Long id) {
        return administradorRepository.findById(id).orElse(null);
    }

    public List<Administrador> listarTodos(Long folhaPagamentoId) {
        return administradorRepository.findAll();
    }


    public Funcionario contratarFuncionario(Long candidatoId) {
        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado!"));
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(candidato.getNome());
        funcionario.setCargo(candidato.getCargo());
        funcionario.setSalario(candidato.getSalarioPretendido());
        candidatoRepository.delete(candidato);
        return funcionarioRepository.save(funcionario);
    }

    public void demitirFuncionario(Long funcionarioId) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado!"));
        funcionarioRepository.delete(funcionario);
    }

    public FolhaPagamento fecharFolhaPagamento(Long folhaPagamentoId){        
        FolhaPagamento folha = folhaPagamentoRepository.findById(folhaPagamentoId)
                .orElseThrow(() -> new RuntimeException("Folha de Pagamento não encontrada!"));

        if (folha.isFechada()) {
            throw new IllegalStateException("Esta folha de pagamento já foi fechada.");
        }

        folha.setFechada(true);
        return folhaPagamentoRepository.save(folha);
    }

}
