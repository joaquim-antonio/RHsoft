package com.exemplo.app.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.CandidatoRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CandidatoService{
    
    @Autowired
    private final CandidatoRepository candidatoRepository;


    public List<Candidato> mostrarTodososCandidato(){
        return(List<Candidato>) candidatoRepository.findAll();
    }

    public Optional<Candidato> buscarCandidatoPorID(Long id){
        return candidatoRepository.findByid(id);
    }


    public Candidato salvarCandidato(Candidato candidato){
        return candidatoRepository.save(candidato);
    }

    

    public Candidato atualizarCandidato(Long id, Candidato candidatoAtualizado){
        Candidato candidato = candidatoRepository.findByid(id).orElseThrow(() -> new IllegalArgumentException(String.format("Usuario não Encontrado com o CPF %s", id)));

        if(candidato.getExperiencias() != null)
            candidato.setExperiencias(candidatoAtualizado.getExperiencias());

        if (candidato.getFormacao() != null)
            candidato.setFormacao(candidatoAtualizado.getFormacao());

        if (candidato.getHabilidades() != null)
            candidato.setHabilidades(candidatoAtualizado.getHabilidades());


        return candidatoRepository.save(candidato);
    }




}