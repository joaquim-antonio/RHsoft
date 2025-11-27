package com.exemplo.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.Comunicado;

public interface ComunicadoRepository extends  JpaRepository<Comunicado, Long>{

    List<Comunicado> findTop5ByOrderByDataPublicacaoDesc();
    
    Page<Comunicado> findAll(Pageable pageable);

}
