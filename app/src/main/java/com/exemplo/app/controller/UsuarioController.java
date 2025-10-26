package com.exemplo.app.controller;

import com.exemplo.app.model.Usuario;
import com.exemplo.app.service.UsuarioService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerenciamento de usuarios do sistema RHSoft.
 * 
 * Expõe endpoints para operacoes CRUD de usuarios.
 * 
 * @author Manus
 * @version 1.0
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuarios com paginacao.
     *
     * @param pageable configuracao de paginacao
     * @return pagina de usuarios
     */
    @GetMapping
    public ResponseEntity<Page<Usuario>> listarTodos(Pageable pageable) {
        Page<Usuario> usuarios = usuarioService.findAll(pageable);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca um usuario pelo ID.
     *
     * @param id ID do usuario
     * @return usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.getById(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Cria um novo usuario.
     *
     * @param usuario dados do usuario
     * @param funcionarioId ID do funcionario (opcional)
     * @return usuario criado
     */
    @PostMapping
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario,
                                        @RequestParam(required = false) Long funcionarioId) {
        Usuario novoUsuario = usuarioService.create(usuario, funcionarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    /**
     * Atualiza um usuario existente.
     *
     * @param id ID do usuario
     * @param usuario dados atualizados
     * @param funcionarioId ID do funcionario (opcional)
     * @return usuario atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id,
                                            @Valid @RequestBody Usuario usuario,
                                            @RequestParam(required = false) Long funcionarioId) {
        Usuario atualizado = usuarioService.update(id, usuario, funcionarioId);
        return ResponseEntity.ok(atualizado);
    }

    /**
     * Deleta um usuario.
     *
     * @param id ID do usuario
     * @return sem conteudo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca usuarios ativos.
     *
     * @param pageable configuracao de paginacao
     * @return pagina de usuarios ativos
     */
    @GetMapping("/ativos")
    public ResponseEntity<Page<Usuario>> buscarAtivos(Pageable pageable) {
        Page<Usuario> usuarios = usuarioService.findByAtivo(pageable);
        return ResponseEntity.ok(usuarios);
    }
}

