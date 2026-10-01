package com.exemplo.app.controller;

import com.exemplo.app.dto.DashboardDistribuicaoDTO;
import com.exemplo.app.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Indicadores e agregações para o painel administrativo")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(
        summary = "Obter distribuição por departamento",
        description = "Retorna a distribuição de funcionários por departamento para exibição em gráficos do painel."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Distribuição retornada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "403", description = "Perfil não tem permissão para acessar este recurso.")
    })
    @GetMapping("/distribuicao")
    public ResponseEntity<DashboardDistribuicaoDTO> getDistribuicao() {
        DashboardDistribuicaoDTO data = dashboardService.getDistribuicaoDepartamentos();
        return ResponseEntity.ok(data);
    }
}