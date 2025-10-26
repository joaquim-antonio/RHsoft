package com.exemplo.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração de segurança da aplicação RHSoft.
 * 
 * Define a política de autenticação baseada em JWT, configurando:
 * - Endpoints públicos e protegidos
 * - Política de sessão (stateless)
 * - Filtro de autenticação JWT
 * - Codificação de senhas com BCrypt
 * 
 * @author Manus
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    /**
     * Configura a cadeia de filtros de segurança HTTP.
     *
     * @param http configuração HTTP
     * @throws Exception exceção de configuração
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    // Endpoints públicos de autenticação
                    .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/auth/validate").permitAll()
                    
                    // Endpoints públicos de vagas e candidatos
                    .antMatchers(HttpMethod.GET, "/api/vagas/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/candidatos/**").permitAll()
                    
                    // Console H2 (apenas desenvolvimento)
                    .antMatchers("/h2-console/**").permitAll()
                    
                    // Todos os outros endpoints requerem autenticação
                    .anyRequest().authenticated();

        // Permitir frames para H2 Console
        http.headers().frameOptions().sameOrigin();

        // Adicionar filtro JWT
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Configura o gerenciador de autenticação.
     *
     * @param auth builder de autenticação
     * @throws Exception exceção de configuração
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Cria o bean do filtro de autenticação JWT.
     *
     * @return filtro de autenticação JWT
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * Cria o bean do codificador de senhas usando BCrypt.
     *
     * @return codificador de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o bean do gerenciador de autenticação.
     *
     * @return gerenciador de autenticação
     * @throws Exception exceção de configuração
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configura CORS para permitir requisições de diferentes origens.
     *
     * @return configuração de CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

