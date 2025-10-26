package com.exemplo.app.exception;

/**
 * Exceção customizada para recursos não encontrados.
 * 
 * Utilizada quando uma entidade ou recurso solicitado não existe no banco de dados.
 * 
 * @author Manus
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem.
     *
     * @param message mensagem de erro
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message mensagem de erro
     * @param cause causa da exceção
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor para recurso não encontrado por ID.
     *
     * @param resourceName nome do recurso
     * @param fieldName nome do campo
     * @param fieldValue valor do campo
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado(a) com %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

