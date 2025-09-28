package com.exemplo.app.model;

import com.exemplo.app.dto.RequestPessoa;
import com.exemplo.app.model.Enums.TipoGenero;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "cpf")
public class Pessoa {

    @Id
    private String cpf;

    @NotBlank
    private String nome;

    @NotBlank
    private String sobrenome;

    @NotBlank
    private String endereco;

    @NotBlank
    private String telefone;

    @NotNull
    private TipoGenero sexo;

    @NotNull
    @Past
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    public Pessoa(RequestPessoa requestPessoa){
        this.cpf = requestPessoa.cpf();
        this.nome = requestPessoa.nome();
        this.sobrenome = requestPessoa.sobrenome();
        this.endereco = requestPessoa.endereco();
        this.telefone = requestPessoa.telefone();
        this.dataNascimento = requestPessoa.dataNascimento();
        this.sexo = requestPessoa.sexo();
    }

    public String getNomeCompleto(){
        return nome + " " + sobrenome;
    }

    public int calcularIdade(){
        LocalDate hoje = LocalDate.now();
        return Period.between(dataNascimento, hoje).getYears();
    }
}
