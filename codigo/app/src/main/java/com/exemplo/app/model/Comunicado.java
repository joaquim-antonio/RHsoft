package com.exemplo.app.model;

import java.time.LocalDate;

import com.exemplo.app.model.Enums.TipoComunicado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "comunicado")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comunicado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "titulo", nullable=false, length=50)
    private String titulo;

    @Column(name = "conteudo", nullable=false, length=50)    
    private String conteudo;

    @Column(name = "data_publicacao", nullable=false)
    private LocalDate dataPublicacao;

    @Enumerated(EnumType.STRING)
    private TipoComunicado tipo;
}
