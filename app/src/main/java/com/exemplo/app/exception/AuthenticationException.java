package com.exemplo.app.exception;

/**
 * Exceção customizada para erros de autenticação.
 * 
 * Utilizada para encapsular erros relacionados ao processo de autenticação,
 * incluindo credenciais inválidas, tokens expirados, etc.
 * 
 * @author Manus
 * @version 1.0
 */
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem.
     *
     * @param message mensagem de erro
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message mensagem de erro
     * @param cause causa da exceção
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

