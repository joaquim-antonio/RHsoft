package com.exemplo.app.config;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.exemplo.app.dto.CboOcupacaoDTO;
import com.exemplo.app.model.Cargo;
import com.exemplo.app.model.Enums.Role;
import com.exemplo.app.repository.CargoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Cargos de admin
    private static final Set<String> TITULOS_ADMIN = Set.of(
            "Administrador",
            "Analista de Recursos Humanos",
            "Gerente de Recursos Humanos",
            "Gerente Administrativo"

    );

    @Override
    public void run(String... args) throws Exception {

        if (cargoRepository.count() > 0) {
            System.out.println("Cargos já foram carregados. Pulando o 'DatabaseLoader'.");
            return;
        }

        System.out.println("Iniciando o carregamento de Cargos do JSON...");

        TypeReference<List<CboOcupacaoDTO>> typeReference = new TypeReference<>() {
        };

        InputStream inputStream = new ClassPathResource("CBO2002 - Ocupacao.json").getInputStream();

        List<CboOcupacaoDTO> ocupacoes = objectMapper.readValue(inputStream, typeReference);

        for (CboOcupacaoDTO dto : ocupacoes) {

            String titulo = dto.getTitulo();

            //Verifica se o título é nulo ou vazio
            if (titulo == null || titulo.trim().isEmpty()) {
                System.out.println("Pulando CBO com título nulo ou vazio (Código: " + dto.getCodigo() + ")");
                continue;
            }

            Role role = TITULOS_ADMIN.contains(titulo) ? Role.ADMIN : Role.USER;

            Cargo cargo = new Cargo();
            cargo.setNome(titulo);
            cargo.setRole(role);

            cargoRepository.save(cargo);
        }

        System.out.println(ocupacoes.size() + " cargos foram lidos (alguns podem ter sido pulados).");
    }
}