package com.exemplo.app.service;
import java.util.List;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.repository.candidatoRepository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.candidatoRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor;
@Service

public class candidatoService{
    @autoWired

    private final candidatoRepository candidatorepository;


    public List<Candidato> mostrarTodososCandidato(){
        return(List<Candidato>) canditadoRepository.findAll();
    }

    public Optional<Candidato> buscarCandidatoPorID(Long id){
        return candidatorepository.findById(id);
    }


    public Candidato salvarCandidato(Candidato candidato){
        return candidatoRepository.save(candidato);
    }

    public void excluirCandidato(Long id){
        candidatoRepository.deleteById(id);
    }


}