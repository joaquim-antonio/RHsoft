package com.exemplo.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class Endereco {

    @Positive
    private String numero;

    @NotBlank
    private String rua;

    private String complemento;

    @NotBlank
    private String bairro;

    @NotBlank
    private String logradouro;

    @NotBlank
    private String cep;

    @NotBlank
    private String cidade;

    @NotBlank
    private String estado;

    public String formatarEndereco() {

        StringBuilder sb = new StringBuilder();

        sb.append(this.logradouro)
                .append(" ")
                .append(this.rua)
                .append(", ")
                .append(this.numero);

        if (this.complemento != null && !this.complemento.trim().isEmpty()) {
            sb.append(", ")
                    .append(this.complemento.trim());
        }

        sb.append(" - ").append(this.bairro);

        sb.append(" - ")
                .append(this.cidade)
                .append("/")
                .append(this.estado);

        sb.append(" - CEP: ").append(this.cep);

        return sb.toString();
    }

}
