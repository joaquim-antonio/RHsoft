package com.exemplo.app.infra.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.exemplo.app.model.Funcionario;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final String SEGREDO = "segredo-de-teste-para-o-token-service";

    @Mock
    private Funcionario funcionario;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        // TokenService le o segredo via @Value; injetamos por reflexao para nao
        // precisar subir o contexto Spring completo.
        ReflectionTestUtils.setField(tokenService, "secret", SEGREDO);
    }

    @Test
    void generateToken_cpfValido_criaTokenComSubjectECpf() {
        when(funcionario.getCpf()).thenReturn("12345678900");

        String token = tokenService.generateToken(funcionario);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("12345678900", tokenService.validateToken(token));
    }

    @Test
    void validateToken_tokenEmitidoPorOutroSegredo_retornaVazio() {
        when(funcionario.getCpf()).thenReturn("12345678900");
        String token = tokenService.generateToken(funcionario);

        TokenService outro = new TokenService();
        ReflectionTestUtils.setField(outro, "secret", "outro-segredo-completamente-diferente");

        assertEquals("", outro.validateToken(token));
    }

    @Test
    void validateToken_tokenInvalido_retornaVazio() {
        assertEquals("", tokenService.validateToken("token.que.nao.e.jwt"));
    }

    @Test
    void validateToken_issuerErrado_retornaVazio() {
        // Token bem formado, porem assinado com issuer diferente do esperado.
        String token = com.auth0.jwt.JWT.create()
                .withIssuer("outro-sistema")
                .withSubject("12345678900")
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(SEGREDO));

        assertEquals("", tokenService.validateToken(token));
    }
}
