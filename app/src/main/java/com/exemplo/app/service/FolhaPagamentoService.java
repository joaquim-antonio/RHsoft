package com.exemplo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Funcionario;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;


@Service
public class FolhaPagamentoService {

@Autowired
private FuncionarioService funcionarioService;

@Autowired
private FuncionarioRepository funcionarioRepository;

@Autowired
private FolhaPagamentoRepository folhaPagamentoRepository;


public void gerarFolhaDePagamento(){
    List<Funcionario> funcionarios = funcionarioService.listarTodosFuncionarios();
}
    






    
}
