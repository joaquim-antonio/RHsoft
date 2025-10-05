package com.exemplo.app.controller;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.service.CandidatoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/Candidato")

public class CandidatoController{

    @autowired;

    private final CandidatoService candidatoservice;

    @GetMapping(path = "/all")
    public ResponseEntity<List<Candidato>> listarCandidatos(){
        List<Candidato> candidatos = candidatoservice.listarTodosCandidatos();
        return new ResponseEntity<>(candidatos, HttpStatus.OK);
    }




     @GetMapping(path = "/{id}")
    public ResponseEntityList<Candidato> buscarCandidatoPorId(@PathVariable Long id) {

        Optional<Candidato> candidato = candidatoservice.buscarCandidatoPorId(id);

        if (candidato.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(candidato.get(), HttpStatus.OK);

    }



    
    }
    


}
