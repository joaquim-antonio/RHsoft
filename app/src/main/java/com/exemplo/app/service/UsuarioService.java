// package com.exemplo.app.service;

// import java.util.Optional;

// import org.apache.coyote.BadRequestException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

// // Autor: Pedro Lucas Soares Rezende
// // Projeto entregue como trabalho acadêmico.


// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.exemplo.app.model.Funcionario;
// import com.exemplo.app.model.Pessoa;
// import com.exemplo.app.model.Usuario;
// import com.exemplo.app.repository.FuncionarioRepository;
// import com.exemplo.app.repository.PessoaRepository;
// import com.exemplo.app.repository.UsuarioRepository;

// import io.micrometer.common.util.StringUtils;
// import lombok.AllArgsConstructor;

// @Service
// @AllArgsConstructor
// public class UsuarioService {

//     @Autowired
//     private final UsuarioRepository usuarioRepository;

//     @Autowired
//     private final FuncionarioRepository funcionarioRepository;

//     @Autowired
//     private final PessoaRepository pessoaRepository;
    
//     private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

//     public Optional<Usuario> listarUsuarios(){
//         return usuarioRepository.findAll()
//             .orElseThrow(() -> new NotFoundException("Nenhum uusario encontrado"));
//     } 

//     public Usuario buscarPorId(Long id) {
//         return usuarioRepository.findById(id)
//                 .orElseThrow(() -> new NotFoundException("Usuário com não encontrado."));
//     }

//     public Usuario criarUsuario(Usuario novoUsuario, Long funcionarioId) {

//         novoUsuario.setPasswordHash(encoder.encode(novoUsuario.getPasswordHash()));
        
//         vincularFuncionario(novoUsuario, funcionarioId);

//         return usuarioRepository.save(novoUsuario);
//     }
   
//     public void atualizarSenha(Long usuarioId, String novaSenha) {
//         if (!StringUtils.hasText(novaSenha)) {
//             throw new BadRequestException("A nova senha não pode ser vazia ou nula.");
//         }

//         Usuario usuario = buscarPorId(usuarioId);

//         String senhaCodificada = encoder.encode(novaSenha);

//         usuario.setPasswordHash(senhaCodificada);

//         usuarioRepository.save(usuario);
//     }

//     public void deletarUsuario(Long id) {
//         Usuario usuario = buscarPorId(id); 
        
//         if (usuario.getFuncionario() != null) {
//             usuario.getFuncionario().setUsuario(null);
//             usuario.setFuncionario(null);
//         }
        
//         usuarioRepository.delete(usuario);
//     }

//     private void vincularFuncionario(Usuario usuario, Long funcionarioId) {
//         if (funcionarioId != null) {
//             Pessoa f = pessoaRepository.findById(funcionarioId)
//                     .orElseThrow(() -> new NotFoundException("Funcionário não encontrado para vincular."));
            
//             usuario.setFuncionario(f);
//             f.setUsuario(usuario);
//         } else {
//             if(usuario.getFuncionario() != null) {
//                 usuario.getFuncionario().setUsuario(null);
//                 usuario.setFuncionario(null);
//             }
//         }
//     }

// }
