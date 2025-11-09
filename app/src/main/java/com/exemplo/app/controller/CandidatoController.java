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

    @Autowired
    private final CandidatoService candidatoservice;

    @GetMapping(path = "/all")
    public ResponseEntity<List<Candidato>> listarCandidatos(){
        List<Candidato> candidatos = candidatoservice.listarTodosOsCandidatos();
        return new ResponseEntity<>(candidatos, HttpStatus.OK);
    }




     @GetMapping("/{cpf}")
public ResponseEntity<?> buscarCandidatoPorCpf(@PathVariable String cpf) {
    Optional<Candidato> candidato = candidatoservice.buscarCandidatoPorCpf(cpf);

    if (candidato.isPresent()) {
        return ResponseEntity.ok(candidato.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(String.format("Candidato não encontrado com o CPF %s", cpf));
    }
}

    @PostMapping
    public ResponseEntity<Candidato> adicionarCandidatos(@Valid @RequestBody Candidato candidato){
        Candidato novoCandidato = candidatoservice.salvarCandidato(candidato);
        return new ResponseEntity<>(novoCandidato, HttpStatus.CREATED);
    }



    @PutMapping(path = "/{id}")
    public ResponseEntity<?> atualizarCandidato(@PathVariable String cpf, @RequestBody Candidato candidatoAtualizado) {

        try {

            Candidato candidato = candidatoservice.atualizarCandidato(cpf, candidatoAtualizado);
            return new ResponseEntity<>(candidato, HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    




    }
 




    
    
    



