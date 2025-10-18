package com.exemplo.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
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
    @Setter(AccessLevel.NONE)
    private String codigo;

    @NotBlank
    private String nome;


}
