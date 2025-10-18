package com.exemplo.app.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "Cargo")
@NoArgsConstructor
@Setter
@Getter
public class Cargo {

    @Id
    @Column (unique = true, nullable = false)
    @NotBlank
    private String codigo;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    @OneToMany (mappedBy = "cargo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private  List<Funcionario> funcionario;

}
