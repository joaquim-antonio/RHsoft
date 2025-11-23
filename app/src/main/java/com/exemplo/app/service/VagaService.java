package com.exemplo.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.dto.RequestVagaDTO;
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

    public List<Vaga> listarTodasVagas() {
        return vagaRepository.findAll();
    }

    public List<Vaga> listarVagasDisponiveis() {
        return vagaRepository.findAll().stream()
                .filter(v -> !v.getDataLimite().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public Vaga buscarVagaPorId(Long id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com ID: " + id));
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

        if (vagaAtualizada.dataLimite().isBefore(LocalDate.now())) {
             throw new IllegalArgumentException("A nova data limite não pode ser no passado.");
        }

        if (!vagaAtual.getCargo().getCodigo().equals(vagaAtualizada.cargoId())) {
            Cargo novoCargo = cargoRepository.findById(vagaAtualizada.cargoId())
                .orElseThrow(() -> new EntityNotFoundException("Novo cargo não encontrado"));
            vagaAtual.setCargo(novoCargo);
        }

        if (!vagaAtual.getDepartamento().getCodigo().equals(vagaAtualizada.departamentoId())) {
            Departamento novoDepto = departamentoRepository.findById(vagaAtualizada.departamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Novo departamento não encontrado"));
            vagaAtual.setDepartamento(novoDepto);
        }

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
        // Verificar se existe candidatos incritos
        Vaga vaga = buscarVagaPorId(id);
        if (vaga.getCandidatura() != null && !vaga.getCandidatura().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir esta vaga pois já existem candidatos inscritos. Considere alterar a data limite para fechar a vaga.");
        }

        vagaRepository.deleteById(id);
    }

}
