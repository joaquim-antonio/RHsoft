package com.exemplo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.LoginFuncionarioDTO;
import com.exemplo.app.dto.LoginResponseDTO;
import com.exemplo.app.dto.RegisterCandidatoDTO;
import com.exemplo.app.dto.RegisterFuncionarioDTO;
import com.exemplo.app.infra.security.TokenService;
import com.exemplo.app.model.Pessoa;
import com.exemplo.app.service.CandidatoService;
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
    private CandidatoService candidatoService; 

    @Autowired
    private TokenService tokenService;

   @PostMapping("/login")
   public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginFuncionarioDTO body) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(body.cpf(), body.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        Pessoa pessoaAuthenticated = (Pessoa) auth.getPrincipal();

        var token = tokenService.generateToken(pessoaAuthenticated);

        String role = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst() 
            .orElse("ROLE_USER"); 
        
        return ResponseEntity.ok(new LoginResponseDTO(
            pessoaAuthenticated.getCpf(), 
            pessoaAuthenticated.getNome(), 
            token, 
            role
        ));
    }

    @Transactional
    @PostMapping("/register-funcionario") 
    public ResponseEntity<String> registerFuncionario(@RequestBody RegisterFuncionarioDTO body) {
        
        funcionarioService.register(body);

        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/register-candidato")
    public ResponseEntity<String> registerCandidato(@RequestBody RegisterCandidatoDTO body) {
        candidatoService.register(body);

        return ResponseEntity.ok().build();
    }
}