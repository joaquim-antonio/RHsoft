package com.exemplo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.exemplo.app.dto.CandidatoProfileDTO;
import com.exemplo.app.dto.RegisterCandidatoDTO;
import com.exemplo.app.exception.CpfAlreadyExistsException;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.CandidatoRepository;
import com.exemplo.app.repository.PessoaRepository;

import jakarta.persistence.EntityNotFoundException;
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

    public CandidatoProfileDTO buscarPerfilPorCpf(String cpf) {
        Candidato c = candidatoRepository.findById(cpf)
            .orElseThrow(() -> new EntityNotFoundException("Candidato não encontrado"));
        
        return new CandidatoProfileDTO(
            c.getCpf(),
            c.getNome(),
            c.getSobrenome(), 
            c.getTelefone(),
            c.getHabilidades(),
            c.getFormacao(),
            c.getExperiencias()
        );
    }

    @Transactional
    public Candidato atualizarPerfil(String cpf, CandidatoProfileDTO dto) {
        Candidato candidato = candidatoRepository.findById(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Candidato não encontrado: " + cpf));

        // Atualiza listas (Limpando e readicionando para garantir sincronia)
        if (dto.habilidades() != null) {
            candidato.getHabilidades().clear();
            candidato.getHabilidades().addAll(dto.habilidades());
        }
        
        if (dto.formacao() != null) {
            candidato.getFormacao().clear();
            candidato.getFormacao().addAll(dto.formacao());
        }

        if (dto.experiencias() != null) {
            candidato.getExperiencias().clear();
            candidato.getExperiencias().addAll(dto.experiencias());
        }

        if (StringUtils.hasText(dto.nome())) {
            candidato.setNome(dto.nome().trim());
        }
        
        if (dto.sobrenome() != null) {
            candidato.setSobrenome(dto.sobrenome().trim());
        }

        if (StringUtils.hasText(dto.telefone())) {
            String foneLimpo = dto.telefone().replaceAll("\\D", "");
            
            if (!foneLimpo.isEmpty()) {
                candidato.setTelefone(foneLimpo);
            }
        }

        // Atualiza dados básicos se vierem
        if(dto.nome() != null) candidato.setNome(dto.nome());
        if(dto.sobrenome() != null) candidato.setSobrenome(dto.sobrenome());
        if(dto.telefone() != null) candidato.setTelefone(dto.telefone());

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