package com.exemplo.app.service;

import com.exemplo.app.dto.LoginRequest;
import com.exemplo.app.dto.LoginResponse;
import com.exemplo.app.dto.RegisterRequest;
import com.exemplo.app.exception.AuthenticationException;
import com.exemplo.app.exception.ResourceNotFoundException;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.UsuarioRepository;
import com.exemplo.app.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de autenticação do sistema RHSoft.
 * 
 * Responsável por:
 * - Validar credenciais de usuário
 * - Gerar tokens JWT
 * - Gerenciar o ciclo de vida de autenticação
 * 
 * @author Manus
 * @version 1.0
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Autentica um usuário e retorna um token JWT.
     *
     * @param loginRequest credenciais de login (username e password)
     * @return resposta contendo o token JWT
     * @throws BadCredentialsException se as credenciais forem inválidas
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            logger.info("Tentativa de login para usuário: {}", loginRequest.getUsername());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);
            
            logger.info("Login bem-sucedido para usuário: {}", loginRequest.getUsername());
            
            return new LoginResponse(token, loginRequest.getUsername());
        } catch (BadCredentialsException e) {
            logger.error("Falha no login para usuário: {} - Credenciais inválidas", loginRequest.getUsername());
            throw new BadCredentialsException("Credenciais inválidas: username ou password incorretos", e);
        } catch (Exception e) {
            logger.error("Erro inesperado durante login para usuário: {}", loginRequest.getUsername(), e);
            throw new AuthenticationException("Erro ao processar login", e);
        }
    }

    /**
     * Registra um novo usuário no sistema.
     *
     * @param registerRequest dados de registro (username e password)
     * @return resposta contendo o token JWT do novo usuário
     * @throws IllegalArgumentException se o username já existir
     */
    public LoginResponse register(RegisterRequest registerRequest) {
        logger.info("Tentativa de registro para usuário: {}", registerRequest.getUsername());
        
        // Verifica se o username já existe
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            logger.warn("Tentativa de registro com username já existente: {}", registerRequest.getUsername());
            throw new IllegalArgumentException("Username já está em uso");
        }

        // Cria novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(registerRequest.getUsername());
        novoUsuario.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        novoUsuario.setAtivo(true);

        // Salva no banco de dados
        usuarioRepository.save(novoUsuario);
        
        logger.info("Usuário registrado com sucesso: {}", registerRequest.getUsername());

        // Gera token para o novo usuário
        String token = tokenProvider.generateTokenFromUsername(novoUsuario.getUsername());
        
        return new LoginResponse(token, novoUsuario.getUsername());
    }

    /**
     * Valida um token JWT.
     *
     * @param token token JWT a validar
     * @return true se o token é válido
     */
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    /**
     * Extrai o username de um token JWT.
     *
     * @param token token JWT
     * @return username contido no token
     */
    public String getUsernameFromToken(String token) {
        return tokenProvider.getUsernameFromToken(token);
    }

    /**
     * Obtém o usuário autenticado a partir do token JWT.
     *
     * @param token token JWT
     * @return usuário autenticado
     */
    public Usuario getAuthenticatedUser(String token) {
        String username = tokenProvider.getUsernameFromToken(token);
        if (username == null) {
            throw new AuthenticationException("Token inválido");
        }
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", username));
    }

    /**
     * Renova um token JWT.
     *
     * @param token token JWT a ser renovado
     * @return novo token JWT
     * @throws AuthenticationException se o token for inválido
     */
    public LoginResponse refreshToken(String token) {
        logger.debug("Tentativa de renovação de token");
        
        if (!tokenProvider.validateToken(token)) {
            logger.warn("Tentativa de renovação com token inválido");
            throw new AuthenticationException("Token inválido ou expirado");
        }

        String username = tokenProvider.getUsernameFromToken(token);
        if (username == null) {
            throw new AuthenticationException("Não foi possível extrair username do token");
        }

        // Verifica se o usuário ainda existe e está ativo
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", username));

        if (!usuario.isAtivo()) {
            throw new AuthenticationException("Usuário inativo");
        }

        // Gera novo token
        String newToken = tokenProvider.generateTokenFromUsername(username);
        
        logger.info("Token renovado com sucesso para usuário: {}", username);
        
        return new LoginResponse(newToken, username);
    }
}

