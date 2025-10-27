package com.exemplo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.LoginFuncionarioDTO;
import com.exemplo.app.dto.LoginResponseDTO;
import com.exemplo.app.dto.RegisterFuncionarioDTO;
import com.exemplo.app.infra.security.TokenService;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.service.FuncionarioService;

import jakarta.transaction.Transactional;


@RestController
@RequestMapping( "/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginFuncionarioDTO body) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(body.cpf(), body.password());
        System.out.format("\n\nbody.cpf/ password: %S e %S", body.cpf(),body.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Funcionario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(auth.getName(),token));
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterFuncionarioDTO body) {
        
        funcionarioService.register(body);

        return ResponseEntity.ok().build();
    }
}

