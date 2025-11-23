package com.exemplo.app.controller;

import com.exemplo.app.model.Pessoa;
import com.exemplo.app.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/pessoa")
@RestController
public class PessoaController {

    @Autowired
    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public ResponseEntity<List<Pessoa>> listarProdutos(){
        List<Pessoa> pessoas = pessoaService.listarTodasPessoas();
        return new ResponseEntity<>(pessoas, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pessoa> postPessoa(@RequestBody @Valid Pessoa pessoa){
        Pessoa pessoaEmCriacao = pessoaService.salvarPessoa(pessoa);
        return new ResponseEntity<>(pessoaEmCriacao, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{cpf}")
    public ResponseEntity<?> atualizarProduto(@PathVariable String cpf, @RequestBody Pessoa pessoaAtualizada){
        try{
            Pessoa novaPessoa = pessoaService.atualizarPessoa(cpf, pessoaAtualizada);
            return new ResponseEntity<>(novaPessoa, HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{cpf}")
    public ResponseEntity<Void> excluir(@PathVariable("cpf") String cpf){
        pessoaService.excluirPessoa(cpf);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
