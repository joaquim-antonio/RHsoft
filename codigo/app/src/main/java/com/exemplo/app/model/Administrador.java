package com.exemplo.app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Funcionario {

    @OneToMany(mappedBy = "administrador")
    private List<FolhaPagamento> folhaPagamento = new ArrayList<>();
}
