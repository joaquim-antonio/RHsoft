package com.exemplo.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.FuncionarioResponseDTO;
import com.exemplo.app.dto.UpdateFuncionarioDTO;
import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.ContaBancaria;
import com.exemplo.app.model.Departamento;
import com.exemplo.app.model.Endereco;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.model.Enums.TipoInsalubridade;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Pessoa;
import com.exemplo.app.service.PessoaService;

import jakarta.validation.Valid;

@RequestMapping("/api/v1/pessoa")
@RestController
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;


    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarPessoas() {
        List<Pessoa> pessoas = pessoaService.listarTodasPessoas();
        List<FuncionarioResponseDTO> dtos = pessoas.stream()
                .filter(p -> !(p instanceof Candidato)) 
                .map(FuncionarioResponseDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        return pessoaService.buscarPessoaCPF(cpf)
                .map(pessoa -> ResponseEntity.ok(new FuncionarioResponseDTO(pessoa)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pessoa> postPessoa(@RequestBody @Valid Pessoa pessoa){
        Pessoa pessoaEmCriacao = pessoaService.salvarPessoa(pessoa);
        return new ResponseEntity<>(pessoaEmCriacao, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{cpf}")
    public ResponseEntity<?> atualizarPessoa(@PathVariable String cpf, @RequestBody UpdateFuncionarioDTO dto) {
        try {
            Funcionario funcionarioConvertido = converterUpdateDtoParaFuncionario(dto);
            
            Pessoa novaPessoa = pessoaService.atualizarPessoa(cpf, funcionarioConvertido);
            
            return new ResponseEntity<>(new FuncionarioResponseDTO(novaPessoa), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Valor inválido em um dos campos de seleção.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{cpf}")
    public ResponseEntity<Void> excluir(@PathVariable("cpf") String cpf){
        pessoaService.excluirPessoa(cpf);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Funcionario converterUpdateDtoParaFuncionario(UpdateFuncionarioDTO dto) {
        Funcionario f = new Funcionario();
        
        // Dados Pessoais
        f.setNome(dto.nome());
        f.setSobrenome(dto.sobrenome());
        f.setTelefone(dto.telefone());
        f.setDataNascimento(dto.dataNascimento());
        
        // Converte Gênero
        if (dto.sexo() != null && !dto.sexo().isBlank()) {
            f.setSexo(TipoGenero.valueOf(dto.sexo()));
        }

        // Dados Contratuais
        f.setSalario(dto.salario());
        f.setDataAdmissao(dto.dataAdmissao());
        f.setHorasTrabalhadas(dto.horasTrabalhadas());
        
        // IDs de Vínculo
        if (dto.cargoId() != null) {
            Cargo c = new Cargo();
            // Usa reflection para setar o ID diretamente
            try {
                java.lang.reflect.Field fieldId = Cargo.class.getDeclaredField("codigo");
                fieldId.setAccessible(true);
                fieldId.set(c, dto.cargoId());
                f.setCargo(c);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (dto.departamentoId() != null) {
            Departamento d = new Departamento();
            // Usa reflection para setar o ID diretamente
            try {
                java.lang.reflect.Field fieldId = Departamento.class.getDeclaredField("codigo");
                fieldId.setAccessible(true);
                fieldId.set(d, dto.departamentoId());
                f.setDepartamento(d);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (dto.tipoAcrescimo() != null && !dto.tipoAcrescimo().isBlank()) {
            f.setTipoAcrescimo(TipoAcrescimo.valueOf(dto.tipoAcrescimo()));
        } else {
            f.setTipoAcrescimo(null);
        }

        if (dto.tipoInsalubridade() != null && !dto.tipoInsalubridade().isBlank()) {
            f.setTipoInsalubridade(TipoInsalubridade.valueOf(dto.tipoInsalubridade()));
        } else {
            f.setTipoInsalubridade(null);
        }
        
        // Endereço e Banco
        if (dto.endereco() != null) {
            Endereco e = new Endereco();
            e.setCep(dto.endereco().cep());
            e.setRua(dto.endereco().rua());
            e.setNumero(dto.endereco().numero());
            e.setBairro(dto.endereco().bairro());
            e.setCidade(dto.endereco().cidade());
            e.setEstado(dto.endereco().estado());
            e.setLogradouro(dto.endereco().logradouro());
            f.setEndereco(e);
        }

        if (dto.contaBancaria() != null) {
            ContaBancaria c = new ContaBancaria();
            c.setNomeBanco(dto.contaBancaria().nomeBanco());
            c.setAgencia(dto.contaBancaria().agencia());
            c.setNumero(dto.contaBancaria().numero());
            c.setChavePix(dto.contaBancaria().chavePix());
            f.setContaBancaria(c);
        }

        return f;
    }
}