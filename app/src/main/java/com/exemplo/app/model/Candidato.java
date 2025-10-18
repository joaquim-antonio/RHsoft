package com.exemplo.app.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Table(name = "candidatos")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidato extends Pessoa{

    private List<String> habilidades;

    private String formacao;
    
    private String experiencia;

    
}


