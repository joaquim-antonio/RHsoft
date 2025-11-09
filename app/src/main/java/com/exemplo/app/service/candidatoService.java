package com.exemplo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.repository.CandidatoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CandidatoService {

    @Autowired
    private final CandidatoRepository candidatoRepository;

    public List<Candidato> listarTodosOsCandidatos() {
        return candidatoRepository.findAll();
    }

    public Optional<Candidato> buscarCandidatoPorCpf(String cpf) {
        return candidatoRepository.findById(cpf); 
    }

    public Candidato salvarCandidato(Candidato candidato) {
        return candidatoRepository.save(candidato);
    }
    
    public Candidato atualizarCandidato(String cpf, Candidato candidatoAtualizado) {
        Candidato candidato = candidatoRepository.findById(cpf)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Candidato não encontrado com o CPF %s", cpf)));

        if (candidatoAtualizado.getExperiencias() != null)
            candidato.setExperiencias(candidatoAtualizado.getExperiencias());

        if (candidatoAtualizado.getFormacao() != null)
            candidato.setFormacao(candidatoAtualizado.getFormacao());

        if (candidatoAtualizado.getHabilidades() != null)
            candidato.setHabilidades(candidatoAtualizado.getHabilidades());

        

        return candidatoRepository.save(candidato);
    }
}
