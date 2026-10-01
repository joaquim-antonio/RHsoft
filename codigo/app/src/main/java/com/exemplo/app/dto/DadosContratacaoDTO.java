package com.exemplo.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Dados de contratação do candidato aprovado")
public record DadosContratacaoDTO(

        @Schema(description = "Salário base mensal", example = "5000.00", required = true)
        @NotNull @NotNull
        @Positive(message = "O salário deve ser positivo") BigDecimal salario,

        @Schema(description = "Data de admissão", example = "2025-02-01", required = true)
        @NotNull LocalDate dataAdmissao,

        @Schema(description = "Código do cargo a atribuir", example = "252105", required = true)
        @NotNull Long cargoId,

        @Schema(description = "Código do departamento a atribuir", example = "1", required = true)
        @NotNull Long departamentoId,

        @Schema(description = "Carga horária mensal", example = "220.0", required = true)
        @NotNull @Positive(message = "Carga horária deve ser positiva") Double horasTrabalhadas,

        @Schema(description = "Agência bancária", example = "1234", required = true)
        @NotNull String agencia,

        @Schema(description = "Número da conta", example = "56789-0", required = true)
        @NotNull String numeroConta,

        @Schema(description = "Nome do banco", example = "Banco do Brasil", required = true)
        @NotNull String nomeBanco,

        @Schema(description = "Chave PIX para pagamento (opcional)", example = "maria.silva@email.com")
        String chavePix,

        @Schema(description = "Tipo de acréscimo aplicado ao salário", example = "DIRETO")
        TipoAcrescimo tipoAcrescimo,

        @Schema(description = "Classificação de insalubridade", example = "NENHUM")
        TipoInsalubridade tipoInsalubridade) {
}
