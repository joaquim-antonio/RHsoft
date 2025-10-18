package com.exemplo.app.model;

import com.exemplo.app.model.Enums.TipoGenero;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

@Table
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "cpf")
public class Pessoa {

    @Id
    @Column(unique = true)
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Setter(AccessLevel.NONE)
    private String cpf;

    @NotBlank
    private String nome;

    @NotBlank
    private String sobrenome;

    @NotBlank
    private String telefone;

    @NotNull
    private TipoGenero sexo;

    @NotNull
    @Past
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Setter(AccessLevel.NONE)
    private LocalDate dataNascimento;

    public String getNomeCompleto(){
        return nome + " " + sobrenome;
    }

    public int calcularIdade(){
        LocalDate hoje = LocalDate.now();
        return Period.between(dataNascimento, hoje).getYears();
    }

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;
}
