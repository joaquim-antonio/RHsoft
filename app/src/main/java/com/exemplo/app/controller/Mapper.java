package com.example.sirius.controller;

// Autor: Pedro Lucas Soares Rezende
// Projeto entregue como trabalho acadêmico.


import com.example.sirius.dto.*;
import com.example.sirius.model.*;

public final class Mapper {
    private Mapper(){}

    public static Funcionario fromDto(FuncionarioDTO dto){
        FuncionarioModel f = new FuncionarioModel();
        f.setId(dto.id());
        f.setNome(dto.nome());
        f.setCpf(dto.cpf());
        f.setCargo(dto.cargo());
        f.setSalario(dto.salario());
        f.setDataAdmissao(dto.dataAdmissao());
        return f;
    }

    public static FuncionarioDTO toDto(Funcionario f){
        return new FuncionarioDTO(
            f.getId(), f.getNome(), f.getCpf(), f.getCargo(), f.getSalario(), f.getDataAdmissao()
        );
    }

    public static Usuario fromDto(UsuarioDTO dto){
        Usuario u = new Usuario();
        u.setId(dto.id());
        u.setUsername(dto.username());
        u.setEmail(dto.email());
        u.setPasswordHash(dto.password());
        if(dto.role() != null) u.setRole(dto.role());
        if(dto.ativo() != null) u.setAtivo(dto.ativo());
        return u;
    }

    public static UsuarioDTO toDto(Usuario u){
        Long funcId = u.getFuncionario() != null ? u.getFuncionario().getId() : null;
        return new UsuarioDTO(
            u.getId(), u.getUsername(), u.getEmail(), "********", u.getRole(), u.isAtivo(), funcId
        );
    }
}
