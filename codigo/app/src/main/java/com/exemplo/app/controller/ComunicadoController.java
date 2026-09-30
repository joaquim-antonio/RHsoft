package com.exemplo.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.ComunicadoResponseDTO;
import com.exemplo.app.model.Comunicado;
import com.exemplo.app.service.ComunicadoService;

@RestController
@RequestMapping("/api/v1/comunicados")
public class ComunicadoController {

    @Autowired
    private ComunicadoService comunicadoService;

    @GetMapping("/recentes")
    public ResponseEntity<List<ComunicadoResponseDTO>> listarRecentes() {
        List<ComunicadoResponseDTO> comunicados = comunicadoService.listarRecentes()
                .stream()
                .map(ComunicadoResponseDTO::new)
                .toList();
        return ResponseEntity.ok(comunicados);
    }

    @GetMapping
    public ResponseEntity<Page<ComunicadoResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 6, sort = "dataPublicacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Comunicado> pageComunicados = comunicadoService.listarPaginado(pageable);
        
        Page<ComunicadoResponseDTO> dtos = pageComunicados.map(ComunicadoResponseDTO::new);
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComunicadoResponseDTO> buscarPorId(@PathVariable Long id) {
        Comunicado comunicado = comunicadoService.obterComunicadoPorId(id);
        return ResponseEntity.ok(new ComunicadoResponseDTO(comunicado));
    }

    @PostMapping
    public ResponseEntity<ComunicadoResponseDTO> criarComunicado(@RequestBody Comunicado comunicado) {
        Comunicado criado = comunicadoService.criarComunicado(comunicado);
        return ResponseEntity.ok(new ComunicadoResponseDTO(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComunicadoResponseDTO> putMethodName(@PathVariable Long id,
            @RequestBody Comunicado comunicado) {
        Comunicado atualizado = comunicadoService.atualizarComunicado(id, comunicado);
        return ResponseEntity.ok(new ComunicadoResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarComunicado(@PathVariable Long id) {
        comunicadoService.deletarComunicado(id);
        return ResponseEntity.noContent().build();
    }
}
