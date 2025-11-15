package com.exemplo.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CboOcupacaoDTO {

    @JsonProperty("code")

    private String codigo;

    @JsonProperty("name")

    private String titulo;

    // Getters e Setters

    public String getCodigo() {

        return codigo;

    }

    public void setCodigo(String codigo) {

        this.codigo = codigo;

    }

    public String getTitulo() {

        return titulo;

    }

    public void setTitulo(String titulo) {

        this.titulo = titulo;

    }

}