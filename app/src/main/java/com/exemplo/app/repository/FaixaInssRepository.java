package com.exemplo.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.app.model.FaixaInss;

public interface FaixaInssRepository extends JpaRepository<FaixaInss, Long> {

    List<FaixaInss> findAllByOrderByOrdemAsc();

}