package com.exemplo.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "usuarios")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Size(min = 8, max = 120)
    private String passwordHash;

    @NotNull
    private boolean status = true;

    @OneToOne
 @JoinColumn(name = "funcionario_id", foreignKey=@ForeignKey(name = "fk_usuario_funcionario"))    private Funcionario funcionario;
}
