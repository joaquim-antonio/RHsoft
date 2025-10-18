package com.exemplo.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contaBancaria")
@Getter
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ContaBancaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    private String agencia;

    @Column(name = "numero_conta", nullable = false)
    private String numero;

    @Column(name= "nome_banco", nullable = false)
    private String nomeBanco;

    @Column(name = "chave_pix", unique = true)
    private String chavePix;

    @OneToOne
    @JoinColumn(name= "funcionario_id")
    private Funcionario funcionario;

    public void atualizarChavePix(String novaChave){
        this.chavePix = novaChave;
    }

}
