package com.exemplo.app.service;

import java.time.LocalDate;
import java.util.List;

import com.exemplo.app.dto.RequestVagaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Vaga;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.DepartamentoRepository;
import com.exemplo.app.repository.VagaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VagaService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private VagaRepository vagaRepository;

    public List<Vaga> listarTodasVagas(){
        return (List<Vaga>) vagaRepository.findAll();
    }

    public Vaga buscarVagaPorId(Long id){
        return vagaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException());
    }

    public Vaga criarVaga(RequestVagaDTO vagaBody){
        if (vagaBody.dataLimite().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Data inválida");
        }

        Cargo cargo = cargoRepository.findById(vagaBody.cargoId())
            .orElseThrow(() -> new EntityNotFoundException("Cargo não cadastrado"));

        Departamento departamento = departamentoRepository.findById(vagaBody.departamentoId())
            .orElseThrow(() -> new EntityNotFoundException("Departamento não cadastrado"));

        Vaga newVaga = new Vaga();
        newVaga.setFuncao(vagaBody.funcao());
        newVaga.setTitulo(vagaBody.titulo());
        newVaga.setDescricao(vagaBody.descricao());
        newVaga.setDataLimite(vagaBody.dataLimite());
        newVaga.setCargo(cargo);
        newVaga.setDepartamento(departamento);

        return vagaRepository.save(newVaga);
    }

    public Vaga atualizarVaga(Long id, RequestVagaDTO vagaAtualizada){
        Vaga vagaAtual = buscarVagaPorId(id);

        vagaAtual.setTitulo(vagaAtualizada.titulo());
        vagaAtual.setFuncao(vagaAtualizada.funcao());
        vagaAtual.setDescricao(vagaAtualizada.descricao());
        vagaAtual.setDataLimite(vagaAtualizada.dataLimite());

        return vagaRepository.save(vagaAtual);
    }

    public void excluirVaga(Long id){
        if (!vagaRepository.existsById(id)){
            throw new EntityNotFoundException("Vaga inexistente");
        }
        vagaRepository.deleteById(id);
    }

}
