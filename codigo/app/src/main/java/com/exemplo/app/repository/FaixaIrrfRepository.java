package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.app.model.FaixaIrrf;

public interface FaixaIrrfRepository extends JpaRepository<FaixaIrrf, Long> {
}