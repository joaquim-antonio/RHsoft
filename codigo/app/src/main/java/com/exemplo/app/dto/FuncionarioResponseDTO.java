package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Pessoa;

public record FuncionarioResponseDTO(
    String cpf,
    String nome,
    String sobrenome,
    String telefone,
    String email, // Adicionado futuramente
    String sexo,
    LocalDate dataNascimento,
    BigDecimal salario,
    LocalDate dataAdmissao,
    Double horasTrabalhadas,
    Cargo cargo,
    Departamento departamento,
    TipoAcrescimo tipoAcrescimo,
    TipoInsalubridade tipoInsalubridade,
    EnderecoData endereco,
    BancoData contaBancaria
) {
    public FuncionarioResponseDTO(Pessoa p) {
        this(
            p.getCpf(),
            p.getNome(),
            p.getSobrenome(),
            p.getTelefone(),
            // Se tiver usuário, pega o email/login, senão null (opcional)
            (p.getUsuario() != null) ? "definido" : null, 
            p.getSexo() != null ? p.getSexo().name() : null,
            p.getDataNascimento(),

            // Verifica se é Funcionario para pegar dados contratuais
            (p instanceof Funcionario f) ? f.getSalario() : null,
            (p instanceof Funcionario f) ? f.getDataAdmissao() : null,
            (p instanceof Funcionario f) ? f.getHorasTrabalhadas() : null,
            (p instanceof Funcionario f) ? f.getCargo() : null,
            (p instanceof Funcionario f) ? f.getDepartamento() : null,
            (p instanceof Funcionario f) ? f.getTipoAcrescimo() : null,
            (p instanceof Funcionario f) ? f.getTipoInsalubridade() : null,

            // Mapeia Endereço
            (p.getEndereco() != null) ? new EnderecoData(p.getEndereco()) : null,

            // Mapeia Conta Bancária
            (p instanceof Funcionario f && f.getContaBancaria() != null) ? new BancoData(f.getContaBancaria()) : null
        );
    }

    
    public record EnderecoData(
        String rua, String numero, String bairro, 
        String cidade, String estado, String cep, String logradouro
    ) {
        public EnderecoData(Endereco e) {
            this(e.getRua(), e.getNumero(), e.getBairro(), 
                 e.getCidade(), e.getEstado(), e.getCep(), e.getLogradouro());
        }
    }

    public record BancoData(
        String nomeBanco, String agencia, String numero, String chavePix
    ) {
        public BancoData(ContaBancaria c) {
            this(c.getNomeBanco(), c.getAgencia(), c.getNumero(), c.getChavePix());
        }
    }
}