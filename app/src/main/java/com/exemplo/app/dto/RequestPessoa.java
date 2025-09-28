package com.exemplo.app.dto;

import com.exemplo.app.model.Enums.TipoGenero;
import com.exemplo.app.model.Pessoa;
import java.time.LocalDate;

public record RequestPessoa(String cpf, String nome, String sobrenome,
                            String endereco, String telefone,
                            LocalDate dataNascimento, TipoGenero sexo) {
}
