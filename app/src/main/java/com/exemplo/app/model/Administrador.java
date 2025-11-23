package com.exemplo.app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
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

    @OneToOne(mappedBy = "administrador")
    private FolhaPagamento folhaPagamento;
}
