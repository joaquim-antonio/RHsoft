package com.exemplo.app.controller;

import com.exemplo.app.model.Candidato;
import com.exemplo.app.service.CandidatoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/Candidato")

public class CandidatoController{

    @autowired;

    private final CandidatoService candidatoservice;


}
