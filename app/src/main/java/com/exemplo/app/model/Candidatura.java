package com.exemplo.app.model;

import java.time.LocalDate;

import com.exemplo.app.model.Enums.StatusCandidatura;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidatura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidatura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    private LocalDate data;

    @NotNull
    private StatusCandidatura status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidato_id", foreignKey=@ForeignKey(name = "fk_candidatura_candidato"))
    private Candidato candidato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaga_id", foreignKey=@ForeignKey(name = "fk_candidatura_vaga"))
    private Vaga vaga;

}