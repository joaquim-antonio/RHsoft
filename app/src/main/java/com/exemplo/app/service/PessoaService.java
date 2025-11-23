package com.exemplo.app.service;

import com.exemplo.app.model.Pessoa;
import com.exemplo.app.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    //GET
    public List<Pessoa> listarTodasPessoas(){
        return (List<Pessoa>)pessoaRepository.findAll();
    }

    public Optional<Pessoa> buscarPessoaCPF(String cpf){
        return pessoaRepository.findById(cpf);
    }

    //POST
    public Pessoa salvarPessoa(Pessoa pessoa){
        return pessoaRepository.save(pessoa);
    }

    //DELETE
    public void excluirPessoa(String cpf){
        pessoaRepository.deleteById(cpf);
    }

    //PUT
    public Pessoa atualizarPessoa(String cpf, Pessoa pessoaAtualizada){

        Pessoa pessoa = pessoaRepository.findById(cpf)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("produto nao encontrado com o cpf = %s", cpf)
                ));

        pessoa.setNome(pessoaAtualizada.getNome());

        return pessoaRepository.save(pessoa);

    }

}
