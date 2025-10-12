package com.exemplo.app.service;

import com.exemplo.app.model.Pessoa;
import com.exemplo.app.repository.PessoaRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PessoaService {

    @Autowired
    private final PessoaRepository PessoaRepository;

    //GET
    public List<Pessoa> listarTodasPessoas(){
        return (List<Pessoa>)PessoaRepository.findAll();
    }

    public Optional<Pessoa> buscarPessoaCPF(String cpf){
        return PessoaRepository.findById(cpf);
    }

    //POST
    public Pessoa salvarPessoa(Pessoa pessoa){
        return PessoaRepository.save(pessoa);
    }

    //DELETE
    public void excluirPessoa(String cpf){
        PessoaRepository.deleteById(cpf);
    }

    //PUT
    public Pessoa atualizarPessoa(String cpf, Pessoa pessoaAtualizada){

        Pessoa pessoa = PessoaRepository.findById(cpf)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("produto nao encontrado com o cpf = %s", cpf)
                ));

        pessoa.setNome(pessoaAtualizada.getNome());

        return PessoaRepository.save(pessoa);

    }

}
