package com.exemplo.app.dto;

import java.time.LocalDate;

import com.exemplo.app.model.Comunicado;
import com.exemplo.app.model.Enums.TipoComunicado;

public record ComunicadoResponseDTO(
    Long id,
    String titulo,
    String conteudo,
    LocalDate dataPublicacao,
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
