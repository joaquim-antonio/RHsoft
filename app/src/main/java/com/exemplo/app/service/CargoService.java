package com.exemplo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CargoService {

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // GET
    public List<Cargo> listarTodosCargos() {
        return cargoRepository.findAll();
    }

    public Cargo buscarCargoPorCodigo(String codigo) {
        return cargoRepository.findById(codigo)
            .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado com o código: " + codigo));
    }

    // POST
    public Cargo criarCargo(Cargo cargo) {
        Optional<Cargo> cargoExistente = cargoRepository.findByNome(cargo.getNome());
        if (cargoExistente.isPresent()) {
            throw new IllegalArgumentException("Cargo com nome '" + cargo.getNome() + "' já existe.");
        }

        if (cargoRepository.existsById(cargo.getCodigo())) {
            throw new IllegalArgumentException("Código de cargo '" + cargo.getCodigo() + "' já existe.");
        }

        return cargoRepository.save(cargo);
    }

    // PUT
    public Cargo atualizarCargo(String codigo, Cargo cargoAtualizado) {
        Cargo cargoExistente = buscarCargoPorCodigo(codigo);

        Optional<Cargo> outroCargoComMesmoNome = cargoRepository.findByNome(cargoAtualizado.getNome());
        if (outroCargoComMesmoNome.isPresent() &&
            !outroCargoComMesmoNome.get().getCodigo().equals(codigo)) {
            throw new IllegalArgumentException("O nome '" + cargoAtualizado.getNome() + "' já está em uso por outro cargo.");
        }

        cargoExistente.setNome(cargoAtualizado.getNome());

        return cargoRepository.save(cargoExistente);
    }

    // DELETE
    public void deletarCargo(String codigo) {
        Cargo cargoExcluido = buscarCargoPorCodigo(codigo);

        boolean cargoEmUso = funcionarioRepository.existsByCargoCodigo(codigo);
        if (cargoEmUso) {
            throw new IllegalStateException("Não é possível excluir: o cargo está em uso.");
        }

        cargoRepository.delete(cargoExcluido);
    }
}
