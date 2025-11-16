package com.exemplo.app.controller;

import com.exemplo.app.dto.DashboardDistribuicaoDTO;
import com.exemplo.app.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/distribuicao")
    public ResponseEntity<DashboardDistribuicaoDTO> getDistribuicao() {
        DashboardDistribuicaoDTO data = dashboardService.getDistribuicaoDepartamentos();
        return ResponseEntity.ok(data);
    }
}