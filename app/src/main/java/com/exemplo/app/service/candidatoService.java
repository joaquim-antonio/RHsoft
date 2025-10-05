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

public class CandidatoService{
    @Autowired;

    private final CandidatoRepository candidatorepository;


    public List<Candidato> mostrarTodososCandidato(){
        return(List<Candidato>) canditadorepository.findAll();
    }

    public Optional<Candidato> buscarCandidatoPorID(Long id){
        return candidatorepository.findById(id);
    }


    public Candidato salvarCandidato(Candidato candidato){
        return candidatorepository.save(candidato);
    }

    public void excluirCandidato(Long id){
        candidatorepository.deleteById(id);
    }

    public Candidato atualizarCandidato(Long id, Candidato candidatoAtualizado){
        Candidato candidato = candidatorepository.findById(id);
        orElseThrow(() => IllegalArgumentException(String format("Candidato não encontrado com ID= %d",id) ));
        candidato.setExperiencia(candidatoAtualizado.getExperiencia());
        candidato.setFormacao(candidatoAtualizado.getformacao());
        candidato.setHabilidades(candidatoAtualizado.getHabilidades());
        return candidatorepository.save(candidato);
    }


}