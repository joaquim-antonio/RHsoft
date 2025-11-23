package com.exemplo.app.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.dto.DadosContratacaoDTO;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Candidatura;
import com.exemplo.app.model.Enums.StatusCandidatura;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.repository.CandidatoRepository;
import com.exemplo.app.repository.CandidaturaRepository;
import com.exemplo.app.repository.VagaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CandidaturaService {

    @Autowired
    private CandidaturaRepository candidaturaRepository;

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    @Transactional
    public Candidatura aplicarParaVaga(String cpfCandidato, Long idVaga) {
        // Validar Candidato
        Candidato candidato = candidatoRepository.findById(cpfCandidato)
                .orElseThrow(() -> new EntityNotFoundException("Candidato não encontrado."));

        // Validar Vaga
        Vaga vaga = vagaRepository.findById(idVaga)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada."));

        // Vaga expirada?
        if (vaga.getDataLimite().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Esta vaga já encerrou o período de candidaturas.");
        }

        // Já se candidatou?
        if (candidaturaRepository.existsByCandidatoCpfAndVagaId(cpfCandidato, idVaga)) {
            throw new IllegalStateException("Você já se candidatou para esta vaga.");
        }

        // Criar Candidatura
        Candidatura novaCandidatura = new Candidatura();
        novaCandidatura.setCandidato(candidato);
        novaCandidatura.setVaga(vaga);
        novaCandidatura.setData(LocalDate.now());
        novaCandidatura.setStatus(StatusCandidatura.ABERTA); // Status inicial

        return candidaturaRepository.save(novaCandidatura);
    }

    public List<Candidatura> listarMinhasCandidaturas(String cpf) {
        return candidaturaRepository.findByCandidatoCpf(cpf);
    }

    public List<Candidatura> listarCandidatosDaVaga(Long idVaga) {
        return candidaturaRepository.findByVagaId(idVaga);
    }

    @Transactional
    public Candidatura atualizarStatus(Long idCandidatura, StatusCandidatura novoStatus) {
        Candidatura candidatura = candidaturaRepository.findById(idCandidatura)
                .orElseThrow(() -> new EntityNotFoundException("Candidatura não encontrada."));

        // Impede alterar status de quem já foi aprovado/contratado para evitar
        // inconsistência
        if (candidatura.getStatus() == StatusCandidatura.APROVADA) {
            throw new IllegalStateException(
                    "Não é possível alterar o status de uma candidatura já aprovada e contratada.");
        }

        candidatura.setStatus(novoStatus);
        return candidaturaRepository.save(candidatura);
    }

    @Transactional
    public Candidatura aprovarEContratar(Long idCandidatura, DadosContratacaoDTO dadosContratacao) {
        // Busca a candidatura
        Candidatura candidatura = candidaturaRepository.findById(idCandidatura)
                .orElseThrow(() -> new EntityNotFoundException("Candidatura não encontrada."));

        // Valida se já foi aprovada antes
        if (candidatura.getStatus() == StatusCandidatura.APROVADA) {
            throw new IllegalStateException("Esta candidatura já foi aprovada anteriormente.");
        }

        // Atualiza status da candidatura para APROVADA
        candidatura.setStatus(StatusCandidatura.APROVADA);
        candidaturaRepository.save(candidatura);

        // Realiza a promoção no sistema
        String cpfCandidato = candidatura.getCandidato().getCpf();
        funcionarioService.contratarCandidato(cpfCandidato, dadosContratacao);

        return candidatura;
    }
}
