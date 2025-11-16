package com.exemplo.app.dto;

public record DepartamentoCountDTO(
        String nomeDepartamento,
        long count) {
    public DepartamentoCountDTO(String nomeDepartamento, long count) {
        this.nomeDepartamento = nomeDepartamento != null ? nomeDepartamento : "Sem Depto.";
        this.count = count;
    }
}