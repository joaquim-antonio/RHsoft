package com.exemplo.app.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de renovação de token JWT.
 * 
 * @author Manus
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    
    @NotBlank(message = "Token não pode estar vazio")
    private String token;
}

