package com.exemplo.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração global da documentação OpenAPI (Swagger).
 *
 * A exigência de autenticação NÃO é aplicada globalmente aqui de propósito: cada controller
 * declara {@code @SecurityRequirement(name = "bearerAuth")} apenas quando realmente exige token,
 * de modo que os endpoints públicos (login, cadastro, vagas disponíveis) apareçam sem cadeado
 * no Swagger UI.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RHSoft API",
                version = "v1.0",
                description = "API REST para o sistema de Recursos Humanos (RHSoft). " +
                        "Documenta endpoints para autenticação, gestão de vagas, candidaturas, " +
                        "folha de pagamento, funcionários, comunicados, configurações e dashboard. " +
                        "Utiliza autenticação JWT Bearer.",
                contact = @Contact(
                        name = "RHSoft",
                        url = "https://github.com/joaquim-antonio/RHsoft"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Autenticação via token JWT. Insira o token no formato: Bearer <token>"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de desenvolvimento")
                ));
    }
}