package com.exemplo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Enums.Role;
import com.exemplo.app.repository.CargoRepository;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.persistence.EntityNotFoundException;

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

    public Cargo buscarCargoPorCodigo(Long codigo) {
        return cargoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado com o código: " + codigo));
    }

    // POST
    public Cargo criarCargo(Cargo cargo) {
        Optional<Cargo> cargoExistente = cargoRepository.findByNome(cargo.getNome());
        if (cargoExistente.isPresent()) {
            throw new IllegalArgumentException("Cargo com nome '" + cargo.getNome() + "' já existe.");
        }

        return cargoRepository.save(cargo);
    }

    public Cargo registrarCargo(Cargo cargo, Role role) {

        Optional<Cargo> cargoExistente = cargoRepository.findByNome(cargo.getNome());
        if (cargoExistente.isPresent()) {
            return cargoExistente.get();
        }else{
            Cargo novoCargo = new Cargo();
            //Atualizar para Dto posteriomente
            novoCargo.setNome(cargo.getNome());
            if (role == null){
                novoCargo.setRole(Role.USER); // Apenas teste
            } else{
                novoCargo.setRole(role);
            }
            
            return cargoRepository.saveAndFlush(novoCargo);
        }
    }

    // PUT
    public Cargo atualizarCargo(Long codigo, Cargo cargoAtualizado) {
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
    public void deletarCargo(Long codigo) {
        Cargo cargoExcluido = buscarCargoPorCodigo(codigo);

        boolean cargoEmUso = funcionarioRepository.existsByCargoCodigo(codigo);
        if (cargoEmUso) {
            throw new IllegalStateException("Não é possível excluir: o cargo está em uso.");
        }

        cargoRepository.delete(cargoExcluido);
    }
}
