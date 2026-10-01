package com.exemplo.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exemplo.app.dto.DashboardDistribuicaoDTO;
import com.exemplo.app.dto.DepartamentoCountDTO;
import com.exemplo.app.repository.FuncionarioRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    FuncionarioRepository funcionarioRepository;

    @InjectMocks
    DashboardService service;

    @Test
    void distribuicao_comMaisDeDoisDepartamentos_agrupaOutros() {
        when(funcionarioRepository.count()).thenReturn(10L);
        when(funcionarioRepository.findCountByDepartamento()).thenReturn(List.of(
                new DepartamentoCountDTO("TI", 5L),
                new DepartamentoCountDTO("RH", 3L),
                new DepartamentoCountDTO("Financeiro", 2L)));

        DashboardDistribuicaoDTO dto = service.getDistribuicaoDepartamentos();

        assertEquals(10L, dto.totalFuncionarios());
        assertEquals(3, dto.segmentos().size());
        assertEquals("Outros", dto.segmentos().get(2).nomeDepartamento());
        assertEquals(2L, dto.segmentos().get(2).count());
    }

    @Test
    void distribuicao_semOutros_naoAdicionaSegmento() {
        when(funcionarioRepository.count()).thenReturn(5L);
        when(funcionarioRepository.findCountByDepartamento()).thenReturn(List.of(
                new DepartamentoCountDTO("TI", 5L)));

        DashboardDistribuicaoDTO dto = service.getDistribuicaoDepartamentos();
        assertEquals(1, dto.segmentos().size());
    }
}
