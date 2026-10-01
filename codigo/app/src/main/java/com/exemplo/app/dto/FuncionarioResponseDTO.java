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

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados cadastrais e contratuais de um funcionário")
public record FuncionarioResponseDTO(
    @Schema(description = "CPF do funcionário", example = "12345678900")
    String cpf,

    @Schema(description = "Nome do funcionário", example = "João")
    String nome,

    @Schema(description = "Sobrenome do funcionário", example = "Souza")
    String sobrenome,

    @Schema(description = "Telefone de contato", example = "31988887777")
    String telefone,

    @Schema(description = "Email/login do usuário", example = "jsouza") // Adicionado futuramente
    String email,

    @Schema(description = "Gênero", example = "MASCULINO", allowableValues = {"MASCULINO", "FEMININO", "OUTRO"})
    String sexo,

    @Schema(description = "Data de nascimento", example = "1990-03-15")
    LocalDate dataNascimento,

    @Schema(description = "Salário base mensal", example = "5000.00")
    BigDecimal salario,

    @Schema(description = "Data de admissão", example = "2025-01-01")
    LocalDate dataAdmissao,

    @Schema(description = "Carga horária mensal", example = "220.0")
    Double horasTrabalhadas,

    @Schema(description = "Cargo do funcionário")
    Cargo cargo,

    @Schema(description = "Departamento do funcionário")
    Departamento departamento,

    @Schema(description = "Tipo de acréscimo aplicado ao salário", example = "DIRETO")
    TipoAcrescimo tipoAcrescimo,

    @Schema(description = "Classificação de insalubridade", example = "NENHUM")
    TipoInsalubridade tipoInsalubridade,

    @Schema(description = "Endereço do funcionário")
    EnderecoData endereco,

    @Schema(description = "Conta bancária do funcionário")
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


    @Schema(description = "Endereço do funcionário")
    public record EnderecoData(
        @Schema(description = "Rua", example = "Rua das Flores") String rua,

        @Schema(description = "Número", example = "100") String numero,

        @Schema(description = "Bairro", example = "Centro") String bairro,

        @Schema(description = "Cidade", example = "Belo Horizonte") String cidade,

        @Schema(description = "Estado", example = "MG") String estado,

        @Schema(description = "CEP", example = "30110-000") String cep,

        @Schema(description = "Logradouro", example = "Rua") String logradouro
    ) {
        public EnderecoData(Endereco e) {
            this(e.getRua(), e.getNumero(), e.getBairro(),
                 e.getCidade(), e.getEstado(), e.getCep(), e.getLogradouro());
        }
    }

    @Schema(description = "Dados bancários do funcionário")
    public record BancoData(
        @Schema(description = "Nome do banco", example = "Banco do Brasil") String nomeBanco,

        @Schema(description = "Agência", example = "1234") String agencia,

        @Schema(description = "Número da conta", example = "56789-0") String numero,

        @Schema(description = "Chave PIX", example = "maria.silva@email.com") String chavePix
    ) {
        public BancoData(ContaBancaria c) {
            this(c.getNomeBanco(), c.getAgencia(), c.getNumero(), c.getChavePix());
        }
    }
}
