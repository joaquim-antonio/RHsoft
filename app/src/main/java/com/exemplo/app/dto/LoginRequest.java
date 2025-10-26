package com.exemplo.app.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de login.
 * 
 * Encapsula as credenciais do usuário (username e password) para autenticação.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 60, message = "Username deve ter entre 3 e 60 caracteres")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 120, message = "Senha deve ter entre 8 e 120 caracteres")
    private String password;
}

