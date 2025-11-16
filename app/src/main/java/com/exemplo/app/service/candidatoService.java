package com.exemplo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.exemplo.app.dto.RegisterCandidatoDTO;
import com.exemplo.app.exception.CpfAlreadyExistsException;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.repository.CandidatoRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final PessoaRepository pessoaRepository;

    public List<Candidato> listarTodosOsCandidatos() {
        return candidatoRepository.findAll();
    }

    public Optional<Candidato> buscarCandidatoPorCpf(String cpf) {
        return candidatoRepository.findById(cpf);
    }

    public Candidato salvarCandidato(Candidato candidato) {
        return candidatoRepository.save(candidato);
    }

    /**
     * Método de registro público para um novo Candidato.
     */
    @Transactional
    public Candidato register(RegisterCandidatoDTO body) {
        // Validação
        if (pessoaRepository.existsById(body.cpf())) {
            throw new CpfAlreadyExistsException("CPF já cadastrado");
        }

        // Candidato
        Candidato newCandidato = new Candidato();
        newCandidato.setCpf(body.cpf());
        newCandidato.setNome(body.nome());
        newCandidato.setSobrenome(body.sobrenome());
        newCandidato.setTelefone(body.telefone());
        newCandidato.setSexo(TipoGenero.valueOf(body.sexo()));
        newCandidato.setDataNascimento(body.dataNascimento());

        Endereco newEndereco = body.endereco(); 
        newCandidato.setEndereco(newEndereco);

        //criando usuario
        Usuario newUser = new Usuario();
        newUser.setPasswordHash(new BCryptPasswordEncoder().encode(body.password()));
        newUser.setStatus(true);
        newCandidato.setUsuario(newUser);
        newUser.setPessoa(newCandidato); 

        //JPA salva no db
        return this.candidatoRepository.save(newCandidato);
    }

    public Candidato atualizarCandidato(String cpf, Candidato candidatoAtualizado) {
        Candidato candidato = candidatoRepository.findById(cpf)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Candidato não encontrado com o CPF %s", cpf)));

        if (candidatoAtualizado.getExperiencias() != null)
            candidato.setExperiencias(candidatoAtualizado.getExperiencias());

        if (candidatoAtualizado.getFormacao() != null)
            candidato.setFormacao(candidatoAtualizado.getFormacao());

        if (candidatoAtualizado.getHabilidades() != null)
            candidato.setHabilidades(candidatoAtualizado.getHabilidades());
        

        return candidatoRepository.save(candidato);
    }
}