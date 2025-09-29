package com.exemplo.app.service;
import java.util.List;

import com.exemplo.app.model.CandidatoModel;
import com.exemplo.app.repository.candidatoRepository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.candidatoRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor;
@Service

public class candidatoService{
    @autoWired

    private final candidatoRepository candidatorepository;


    public List<CandidatoModel> mostrarTodososCandidato(){
        return(List<CandidatoModel>) canditadoRepository.findAll();
    }

    public Optional<Candidato> buscarCandidatoPorID(Long id){
        return candidatorepository.findById(id);
    }


}