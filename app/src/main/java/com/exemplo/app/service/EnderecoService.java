package com.exemplo.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Pessoa;
import com.exemplo.app.repository.EnderecoRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EnderecoService {
    
    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;
    
    //GET
    public Endereco buscarEnderecoPorCPF(String cpf){
        Pessoa pessoa = pessoaRepository.findById(cpf)
            .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        
        return pessoa.getEndereco();
    }

    //PUT
    public Endereco atualizarEnderecoDePessoa(String cpf, Endereco enderecoNovo){
        Pessoa pessoa = pessoaRepository.findById(cpf)
            .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

        Endereco enderecoExistente = pessoa.getEndereco();

        enderecoExistente.setLogradouro(enderecoNovo.getLogradouro());
        enderecoExistente.setRua(enderecoNovo.getRua()); 
        enderecoExistente.setNumero(enderecoNovo.getNumero());
        enderecoExistente.setComplemento(enderecoNovo.getComplemento());
        enderecoExistente.setBairro(enderecoNovo.getBairro());
        enderecoExistente.setCidade(enderecoNovo.getCidade());
        enderecoExistente.setEstado(enderecoNovo.getEstado());
        enderecoExistente.setCep(enderecoNovo.getCep());


        return enderecoRepository.save(enderecoExistente);

    }

}
