package com.exemplo.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.UserResponseDTO;
import com.exemplo.app.model.Pessoa;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> getMyUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Não autorizado");
        }

        Pessoa pessoaLogada = (Pessoa) authentication.getPrincipal();

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        UserResponseDTO dto = new UserResponseDTO(
                pessoaLogada.getCpf(),
                pessoaLogada.getNome(),
                pessoaLogada.getSobrenome(),
                role);
        return ResponseEntity.ok(dto);
    }
}