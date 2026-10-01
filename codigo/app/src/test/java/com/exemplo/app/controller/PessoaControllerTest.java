package com.exemplo.app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.service.PessoaService;

@WebMvcTest(PessoaController.class)
class PessoaControllerTest extends BaseControllerTest {

    @MockitoBean
    PessoaService pessoaService;

    private Funcionario funcionario() {
        Funcionario f = new Funcionario();
        f.setCpf("12345678900");
        f.setNome("Joao");
        f.setSobrenome("Souza");
        return f;
    }

    private String jsonPessoaValida() {
        return """
            {
              "cpf": "12345678900",
              "nome": "Joao",
              "telefone": "31988887777",
              "sexo": "MASCULINO",
              "dataNascimento": "1990-03-15",
              "endereco": {
                "rua": "Rua das Flores",
                "numero": "100",
                "bairro": "Centro",
                "cidade": "Belo Horizonte",
                "estado": "MG",
                "cep": "30110-000",
                "logradouro": "Rua"
              }
            }
            """;
    }

    private String jsonAtualizacaoCompleta() {
        return """
            {
              "nome": "Joao",
              "sobrenome": "Souza",
              "telefone": "31988887777",
              "sexo": "MASCULINO",
              "dataNascimento": "1990-03-15",
              "salario": 5000.00,
              "dataAdmissao": "2025-01-01",
              "horasTrabalhadas": 220.0,
              "cargoId": 1,
              "departamentoId": 1,
              "tipoAcrescimo": "INSALUBRIDADE",
              "tipoInsalubridade": "BAIXO",
              "endereco": {
                "rua": "Rua das Flores",
                "numero": "100",
                "bairro": "Centro",
                "cidade": "Belo Horizonte",
                "estado": "MG",
                "cep": "30110-000",
                "logradouro": "Rua"
              },
              "contaBancaria": {
                "nomeBanco": "Banco do Brasil",
                "agencia": "1234",
                "numero": "56789-0",
                "chavePix": "joao@email.com"
              }
            }
            """;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarPessoas_filtraCandidatos_retorna200() throws Exception {
        Funcionario func = funcionario();
        Candidato cand = new Candidato();
        cand.setCpf("99999999999");
        cand.setNome("Maria");

        when(pessoaService.listarTodasPessoas()).thenReturn(List.of(func, cand));

        mockMvc.perform(get("/api/v1/pessoa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorCpf_quandoExiste_retorna200() throws Exception {
        when(pessoaService.buscarPessoaCPF("12345678900")).thenReturn(Optional.of(funcionario()));

        mockMvc.perform(get("/api/v1/pessoa/12345678900"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorCpf_quandoNaoExiste_retorna404() throws Exception {
        when(pessoaService.buscarPessoaCPF("12345678900")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pessoa/12345678900"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarPessoa_comEnumInvalido_retorna400() throws Exception {
        // TipoInsalubridade real e BAIXO/MEDIO/ALTO, mas o Swagger documenta "NENHUM".
        // Enviar o valor documentado estoura IllegalArgumentException no valueOf(),
        // e o controller converte em 400. Divergencia de contrato conhecida.
        when(pessoaService.atualizarPessoa(anyString(), any())).thenReturn(funcionario());

        mockMvc.perform(put("/api/v1/pessoa/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"tipoInsalubridade\": \"NENHUM\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarPessoa_retorna201() throws Exception {
        when(pessoaService.salvarPessoa(any())).thenReturn(funcionario());

        mockMvc.perform(post("/api/v1/pessoa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPessoaValida()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarPessoa_camposObrigatoriosVazios_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pessoa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarPessoa_comDadosCompletos_retorna200() throws Exception {
        when(pessoaService.atualizarPessoa(anyString(), any())).thenReturn(funcionario());

        mockMvc.perform(put("/api/v1/pessoa/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizacaoCompleta()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarPessoa_semEnumsNemIds_retorna200() throws Exception {
        // Cobre os ramos "null/blank" de sexo, tipoAcrescimo, tipoInsalubridade,
        // cargoId, departamentoId, endereco e contaBancaria.
        when(pessoaService.atualizarPessoa(anyString(), any())).thenReturn(funcionario());

        mockMvc.perform(put("/api/v1/pessoa/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "nome": "Joao",
                              "sexo": "",
                              "tipoAcrescimo": "  ",
                              "tipoInsalubridade": null
                            }
                            """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarPessoa_quandoValorInvalido_retorna400() throws Exception {
        when(pessoaService.atualizarPessoa(anyString(), any()))
                .thenThrow(new IllegalArgumentException("cargo invalido"));

        mockMvc.perform(put("/api/v1/pessoa/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarPessoa_quandoOutroErro_retorna400() throws Exception {
        when(pessoaService.atualizarPessoa(anyString(), any()))
                .thenThrow(new RuntimeException("erro generico"));

        mockMvc.perform(put("/api/v1/pessoa/12345678900")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void excluir_retorna204() throws Exception {
        doNothing().when(pessoaService).excluirPessoa("12345678900");

        mockMvc.perform(delete("/api/v1/pessoa/12345678900"))
                .andExpect(status().isNoContent());
    }
}
