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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;


@RestController
@RequestMapping( "/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e cadastro de usuários (candidatos e funcionários)")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private CandidatoService candidatoService; 

    @Autowired
    private TokenService tokenService;

   @Operation(
        summary = "Realizar login",
        description = "Autentica um usuário (funcionário, administrador ou candidato) via CPF e senha. Retorna um token JWT para uso nos demais endpoints."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. Retorna token JWT."),
        @ApiResponse(responseCode = "401", description = "CPF ou senha inválidos.")
    })
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

    @Operation(
        summary = "Cadastrar funcionário",
        description = "Registra um novo funcionário no sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Funcionário cadastrado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já cadastrado.")
    })
    @Transactional
    @PostMapping("/register-funcionario")
    public ResponseEntity<String> registerFuncionario(@RequestBody RegisterFuncionarioDTO body) {
        
        funcionarioService.register(body);

        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Cadastrar candidato",
        description = "Registra um novo candidato no sistema, permitindo que ele se candidate às vagas publicadas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Candidato cadastrado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já cadastrado.")
    })
    @Transactional
    @PostMapping("/register-candidato")
    public ResponseEntity<String> registerCandidato(@RequestBody RegisterCandidatoDTO body) {
        candidatoService.register(body);

        return ResponseEntity.ok().build();
    }
}