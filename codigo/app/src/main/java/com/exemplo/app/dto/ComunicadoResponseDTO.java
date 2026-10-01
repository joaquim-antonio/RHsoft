package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Comunicado;
import com.exemplo.app.model.Enums.TipoComunicado;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um comunicado")
public record ComunicadoResponseDTO(

    @Schema(description = "Código do comunicado", example = "1")
    Long id,

    @Schema(description = "Título do comunicado", example = "Feriado prolongado")
    String titulo,

    @Schema(description = "Conteúdo do comunicado", example = "O expediente na sexta-feira será encerrado às 16h.")
    String conteudo,

    @Schema(description = "Data de publicação", example = "2025-01-10")
    LocalDate dataPublicacao,

    @Schema(description = "Tipo do comunicado", example = "AVISO")
    TipoComunicado tipo
) {
   public ComunicadoResponseDTO(Comunicado comunicado){
         this(
              comunicado.getId(),
              comunicado.getTitulo(),
              comunicado.getConteudo(),
              comunicado.getDataPublicacao(),
              comunicado.getTipo()
         );
   }
}
