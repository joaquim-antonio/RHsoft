package com.exemplo.app.controller;
/*
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

    @Autowired

    private final CandidatoService candidatoservice;

    @GetMapping(path = "/all")
    public ResponseEntity<List<Candidato>> listarCandidatos(){
        List<Candidato> candidatos = candidatoservice.listarTodosCandidatos();
        return new ResponseEntity<>(candidatos, HttpStatus.OK);
    }




     @GetMapping(path = "/{id}")
    public ResponseEntity<List<Candidato>> buscarCandidatoPorId(@PathVariable Long id) {

        Optional<Candidato> candidato = candidatoservice.buscarCandidatoPorId(id);

        if (candidato.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(candidato.get(), HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Candidato> adicionarCandidatos(@Valid @RequestBody Candidato candidato){
        Candidato novoCandidato = candidatoservice.salvarCandidato(candidato);
        return new ResponseEntity<>(novoCandidato, HttpStatus.CREATED);
    }



    @PutMapping(path = "/{id}")
    public ResponseEntity<?> atualizarCandidato(@PathVariable Long id, @RequestBody Candidato candidatoAtualizado) {

        try {

            Candidato candidato = candidatoservice.atualizarCandidato(id, candidatoAtualizado);
            return new ResponseEntity<>(candidato, HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id")Long id){
        candidatoservice.excluirCandidato(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




    }
 */




    
    
    



