package com.exemplo.app.controller;


import com.exemplo.app.dto.LoginFuncionarioDTO;
import com.exemplo.app.dto.LoginResponseDTO;
import com.exemplo.app.dto.RegisterFuncionarioDTO;
import com.exemplo.app.infra.security.TokenService;
import com.exemplo.app.model.*;
import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.service.CargoService;
import com.exemplo.app.service.ContaBancariaService;
import com.exemplo.app.service.DepartamentoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping( "/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private DepartamentoService deptoService;

    @Autowired
    private ContaBancariaService contaService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginFuncionarioDTO body) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(body.cpf(), body.password());
        System.out.format("\n\nbody.cpf/ password: %S e %S", body.cpf(),body.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Funcionario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(auth.getName(),token));
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterFuncionarioDTO body) {
        if(this.repository.findByCpf(body.cpf()) != null){
            return ResponseEntity.badRequest().build();
        }else{

            //criando funcionario
            Funcionario newFuncionario = new Funcionario();
            newFuncionario.setCpf(body.cpf());
            newFuncionario.setNome(body.nome());
            newFuncionario.setSobrenome(body.sobrenome());
            newFuncionario.setTelefone(body.telefone());
            newFuncionario.setSexo(TipoGenero.valueOf(body.sexo()));
            newFuncionario.setDataNascimento(body.dataNascimento());
            newFuncionario.setSalario(body.salario());
            newFuncionario.setDataAdmissao(body.dataAdmissao());
            newFuncionario.setHorasTrabalhadas(body.horasTrabalhadas());
            newFuncionario.setHorasExtras(0.00);

            //criando endereco
            Endereco newEndereco = new Endereco();
            newEndereco.setNumero(body.endereco().getNumero());
            newEndereco.setRua(body.endereco().getRua());
            newEndereco.setComplemento(body.endereco().getComplemento());
            newEndereco.setBairro(body.endereco().getBairro());
            newEndereco.setLogradouro(body.endereco().getLogradouro());
            newEndereco.setCep(body.endereco().getCep());
            newEndereco.setCidade(body.endereco().getCidade());
            newEndereco.setEstado(body.endereco().getEstado());
            newFuncionario.setEndereco(newEndereco);

            //criando usuario
            Usuario newUser = new Usuario();
            newUser.setPasswordHash(new BCryptPasswordEncoder().encode(body.password()));
            newUser.setStatus(true);
            newFuncionario.setUsuario(newUser);
            newUser.setFuncionario(newFuncionario);

            //criando conta bancaria
            ContaBancaria newConta = new ContaBancaria(
                    body.contaBancaria().getAgencia(),
                    body.contaBancaria().getNumero(),
                    body.contaBancaria().getNomeBanco(),
                    body.contaBancaria().getChavePix()
                    );
            newFuncionario.setContaBancaria(newConta);
            newConta.setFuncionario(newFuncionario);

            //criando cargo
            newFuncionario.setCargo(cargoService.registrarCargo(body.cargo()));

            //adicionar departamento
            newFuncionario.setDepartamento(deptoService.registrarDepartamento(body.departamento()));

            //JPA salva no db
            this.repository.save(newFuncionario);
            return ResponseEntity.ok().build();
        }
    }
}

