package com.exemplo.app.model;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CANDIDATO")
public class Candidato extends Pessoa{


    @ElementCollection
    @CollectionTable(name = "candidato_habilidades", joinColumns = @JoinColumn(name ="candidato_id"))
    private List<String> habilidades = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "candidato_formacao", joinColumns = @JoinColumn(name ="candidato_id"))
    private List<String> formacao = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "candidato_experiencias", joinColumns = @JoinColumn(name ="candidato_id"))
    private List <String> experiencias = new ArrayList<>();

    @OneToMany(mappedBy = "candidato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidatura> candidatura;

    
}


