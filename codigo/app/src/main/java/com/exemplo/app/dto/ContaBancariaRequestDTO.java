package com.exemplo.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados bancários de um funcionário")
public record ContaBancariaRequestDTO(

    @Schema(description = "Agência bancária", example = "1234", required = true)
    @NotBlank
    String agencia,

    @Schema(description = "Número da conta", example = "56789-0", required = true)
    @NotBlank
    String numero,

    @Schema(description = "Nome do banco", example = "Banco do Brasil", required = true)
    @NotBlank
    String nomeBanco,

    @Schema(description = "Chave PIX para pagamento (opcional)", example = "maria.silva@email.com")
    String chavePix,

    @Schema(description = "CPF do funcionário dono da conta", example = "12345678900", required = true)
    @NotBlank
    String funcionarioCpf
) {
}
