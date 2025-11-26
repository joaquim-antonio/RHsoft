package com.exemplo.app.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.model.Comunicado;
import com.exemplo.app.repository.ComunicadoRepository;

@Service
public class ComunicadoService {

    @Autowired
    private ComunicadoRepository comunicadoRepository;

    public List<Comunicado> listarRecentes(){
        return comunicadoRepository.findTop5ByOrderByDataPublicacaoDesc();
    }

    public Comunicado obterComunicadoPorId(Long id){
        return comunicadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comunicado com ID " + id + " não encontrado."));
    }
    
    @Transactional
    public Comunicado criarComunicado(Comunicado comunicado){
        comunicado.setDataPublicacao(LocalDate.now());
        return comunicadoRepository.save(comunicado);
    }

    @Transactional
    public Comunicado atualizarComunicado(Long id, Comunicado comunicadoAtualizado){
        // Verifica se o comunicado existe antes de tentar atualizar
        Comunicado comunicadoExistente = comunicadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comunicado com ID " + id + " não encontrado."));
        
        comunicadoExistente.setTitulo(comunicadoAtualizado.getTitulo());
        comunicadoExistente.setConteudo(comunicadoAtualizado.getConteudo());
        comunicadoExistente.setTipo(comunicadoAtualizado.getTipo());
        return comunicadoRepository.save(comunicadoExistente);
    }

    @Transactional
    public void deletarComunicado(Long id){
        // Verifica se o comunicado existe antes de tentar deletar
        if (!comunicadoRepository.existsById(id)) {
            throw new IllegalArgumentException("Comunicado com ID " + id + " não encontrado.");
        }
        comunicadoRepository.deleteById(id);
    }
    
}
