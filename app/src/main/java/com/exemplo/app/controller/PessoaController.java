package com.exemplo.app.controller;

import com.exemplo.app.dto.RequestPessoa;
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

    //nao consegui remover esse construtor, pedir ajuda ao diego
    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public ResponseEntity<List<Pessoa>> listarProdutos(){
        List<Pessoa> pessoas = pessoaService.listarTodasPessoas();
        return new ResponseEntity<>(pessoas, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pessoa> postPessoa(@RequestBody @Valid RequestPessoa data){
        Pessoa pessoa = new Pessoa(data);
        pessoaService.salvarPessoa(pessoa);
        return new ResponseEntity<>(pessoa, HttpStatus.CREATED);
    }
}
