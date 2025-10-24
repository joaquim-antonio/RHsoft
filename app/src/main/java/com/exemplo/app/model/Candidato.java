package com.exemplo.app.model;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Table(name = "Candidatos")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidato extends Pessoa{

    @ElementCollection
    @CollectionTable(name = "candidato_habilidades", joinColumns = @JoinColumn(name ="candidato_id"))
    private List<String> habilidades = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "candidato_formacao", joinColumns = @JoinColumn(name ="candidato_id"))
    private List<String> formacao = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "candidato_experiências", joinColumns = @JoinColumn(name ="candidato_id"))
    private List <String> experiencias = new ArrayList<>();

    
}


