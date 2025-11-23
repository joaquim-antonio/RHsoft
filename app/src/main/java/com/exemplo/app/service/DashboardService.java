package com.exemplo.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.dto.DashboardDistribuicaoDTO;
import com.exemplo.app.dto.DepartamentoCountDTO;
import com.exemplo.app.repository.FuncionarioRepository;

@Service
public class DashboardService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public DashboardDistribuicaoDTO getDistribuicaoDepartamentos() {

        long totalFuncionarios = funcionarioRepository.count();
        List<DepartamentoCountDTO> contagemTotal = funcionarioRepository.findCountByDepartamento();

        List<DepartamentoCountDTO> segmentos = new ArrayList<>();

        List<DepartamentoCountDTO> top2 = contagemTotal.stream().limit(2).collect(Collectors.toList());
        segmentos.addAll(top2);

        long countTop2 = top2.stream().mapToLong(DepartamentoCountDTO::count).sum();
        long countOutros = totalFuncionarios - countTop2;
        if (countOutros > 0) {
            segmentos.add(new DepartamentoCountDTO("Outros", countOutros));
        }
        return new DashboardDistribuicaoDTO(totalFuncionarios, segmentos);
    }
}