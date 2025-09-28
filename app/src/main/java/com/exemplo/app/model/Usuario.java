package com.exemplo.app.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "usuarios")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 60)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(min = 8, max = 120)
    private String passwordHash;
    
    private boolean ativo = true;

    @OneToOne
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;
}
