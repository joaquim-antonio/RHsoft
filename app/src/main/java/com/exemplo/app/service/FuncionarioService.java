package com.exemplo.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.exemplo.app.dto.RegisterFuncionarioDTO;
import com.exemplo.app.exception.CpfAlreadyExistsException;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.FuncionarioRepository;

import jakarta.transaction.Transactional;

//chamada automaticamente pelo spring security ao tentarmos acessar qualquer endpoint
@Service
public class FuncionarioService implements UserDetailsService {






    @Autowired
    FuncionarioRepository repository;

    @Autowired
    DepartamentoService deptoService;

    @Autowired
    CargoService cargoService;


    @Autowired
    private FuncionarioRepository funcionarioRepository;



    
    public List<Funcionario> listarTodosFuncionarios(){
        List<Funcionario> funcionarios = new ArrayList<>();
        funcionarioRepository.findAll().forEach(funcionarios::add);
        return funcionarios;
    }



    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        return this.repository.findByCpf(cpf);
    }

    @Transactional
    public Funcionario register(RegisterFuncionarioDTO body) {
        // Validação
        if (repository.findByCpf(body.cpf()) != null) {
            throw new CpfAlreadyExistsException("CPF já cadastrado");
        }

        // Funcionario
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

        // Endereco
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
        newFuncionario.setCargo(cargoService.registrarCargo(body.cargo(), body.role()));

        //adicionar departamento
        newFuncionario.setDepartamento(deptoService.registrarDepartamento(body.departamento()));

        //JPA salva no db
        return this.repository.save(newFuncionario);
    }
}
