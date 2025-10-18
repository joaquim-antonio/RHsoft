package com.exemplo.app.model;

import java.time.LocalDate;

import com.exemplo.app.model.Enums.StatusCandidatura;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
    private Long id;

    @NotBlank
    private LocalDate data;

    @NotBlank
    private StatusCandidatura status;

}
