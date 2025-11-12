package com.exemplo.app.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vaga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    private String funcao;

    @NotBlank
    private String titulo;

    private String descricao;

    @NotNull
    private LocalDate dataLimite;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cargo_id", foreignKey=@ForeignKey(name = "fk_vaga_cargo"))
    private Cargo cargo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "departamento_id", foreignKey=@ForeignKey(name = "fk_vaga_departamento"))
    private Departamento departamento;

    @OneToMany(mappedBy = "vaga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidatura> candidatura;

}
