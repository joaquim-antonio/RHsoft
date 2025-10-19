package com.exemplo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Departamento;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // GET
    public List<Departamento> listarTodosDepartamentos() {
        return departamentoRepository.findAll();
    }

    public Departamento buscarDepartamentoPorCodigo(String codigo) {
        return departamentoRepository.findById(codigo)
            .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));
    }

    // POST
    public Departamento criarDepartamento(Departamento departamento) {
        Optional<Departamento> departamentoExistente = departamentoRepository.findByNome(departamento.getNome());
        if (departamentoExistente.isPresent()) {
            throw new IllegalArgumentException("Departamento com este nome já existe");
        }
        if (departamentoRepository.existsById(departamento.getCodigo())) {
            throw new IllegalArgumentException("Código de departamento já existente");
        }
        return departamentoRepository.save(departamento);
    }

    // PUT
    public Departamento atualizarDepartamento(String codigo, Departamento departamentoNovo) {
        Departamento departamentoExistente = buscarDepartamentoPorCodigo(codigo);

        Optional<Departamento> outroDepartamentoComMesmoNome = departamentoRepository.findByNome(departamentoNovo.getNome());
        if (outroDepartamentoComMesmoNome.isPresent() && 
            !outroDepartamentoComMesmoNome.get().getCodigo().equals(codigo)) {
            throw new IllegalArgumentException("O nome '" + departamentoNovo.getNome() + "' já está em uso por outro departamento.");
        }

        departamentoExistente.setNome(departamentoNovo.getNome());
        departamentoExistente.setDescricao(departamentoNovo.getDescricao());


        return departamentoRepository.save(departamentoExistente);
    }

    // DELETE
    public void deletarDepartamento(String codigo) {
        Departamento departamentoExcluir = buscarDepartamentoPorCodigo(codigo);

        boolean departamentoEmUso = funcionarioRepository.existsByDepartamentoCodigo(codigo);
        if (departamentoEmUso) {
            throw new IllegalStateException("Não é possível excluir: o departamento está em uso");
        }

        departamentoRepository.delete(departamentoExcluir);
    }
}
