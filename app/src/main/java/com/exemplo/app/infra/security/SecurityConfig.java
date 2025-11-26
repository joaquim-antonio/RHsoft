package com.exemplo.app.infra.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // --- ROTAS PÚBLICAS ---
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register-candidato").permitAll()
                        .requestMatchers("/user/me").authenticated()

                        // --- GESTÃO DE VAGAS ---
                        .requestMatchers(HttpMethod.GET, "/api/v1/vagas/disponiveis").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vagas/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vagas").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/vagas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/vagas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vagas/**").hasRole("ADMIN")

                        // --- ROTAS DE ADMIN ---
                        .requestMatchers(HttpMethod.PUT, "/api/v1/folha-pagamento/pagamento/editar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/folha-pagamento/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/folha-pagamento/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/auth/register-funcionario").permitAll() // teste

                        // --- ROTAS DE CANDIDATO ---
                        .requestMatchers("/api/v1/candidaturas/minhas").hasAnyRole("CANDIDATO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/candidaturas/aplicar/**").hasAnyRole("CANDIDATO")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/candidaturas/cancelar/{vagaId}")
                        .hasAnyRole("CANDIDATO")

                        // --- ROTAS DE ADMINISTRAÇÃO ---
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/candidaturas/**").hasAnyRole("ADMIN")

                        // PERFIL DE CANDIDATO
                        .requestMatchers("/api/v1/Candidato/**").hasAnyRole("CANDIDATO", "ADMIN")

                        // COMUNICADOS
                        .requestMatchers(HttpMethod.GET, "/api/v1/comunicados/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/comunicados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/comunicados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/comunicados/**").hasRole("ADMIN")

                        // Leitura da folha
                        .requestMatchers(HttpMethod.GET, "/api/v1/folha-pagamento/**").hasAnyRole("ADMIN", "USER")

                        .anyRequest().authenticated())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
