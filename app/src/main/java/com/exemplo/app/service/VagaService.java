package com.exemplo.app.service;

import java.time.LocalDate;
import java.util.List;

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

    public Vaga criarVaga(Vaga vaga, String cargoCodigo, Long departamentoCodigo){
        if (vaga.getDataLimite().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Data inválida");
        }
        Cargo cargo = cargoRepository.findById(cargoCodigo)
            .orElseThrow(() -> new EntityNotFoundException("Cargo não cadastrado"));

        Departamento departamento = departamentoRepository.findById(departamentoCodigo)
            .orElseThrow(() -> new EntityNotFoundException("Departamento não cadastrado"));
        vaga.setCargo(cargo);
        vaga.setDepartamento(departamento);
        return vagaRepository.save(vaga);
    }

    public Vaga atualizarVaga(Long id, Vaga vagaAtualizada){
        Vaga vagaAtual = buscarVagaPorId(id);

        vagaAtual.setTitulo(vagaAtualizada.getTitulo());
        vagaAtual.setFuncao(vagaAtualizada.getFuncao());
        vagaAtual.setDescricao(vagaAtualizada.getDescricao());
        vagaAtual.setDataLimite(vagaAtualizada.getDataLimite());

        return vagaRepository.save(vagaAtual);
    }

    public void excluirVaga(Long id){
        if (!vagaRepository.existsById(id)){
            throw new EntityNotFoundException("Vaga inexistente");
        }
        vagaRepository.deleteById(id);
    }

}
