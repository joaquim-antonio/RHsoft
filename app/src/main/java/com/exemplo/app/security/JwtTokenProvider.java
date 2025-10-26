package com.exemplo.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Componente responsável pela geração, validação e extração de informações de tokens JWT.
 * 
 * Implementa as operações de autenticação baseada em tokens para o sistema RHSoft,
 * garantindo segurança através de assinatura criptográfica.
 * 
 * @author Manus
 * @version 1.0
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret:my-super-secret-key-for-jwt-token-generation-and-validation-purpose-only}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:86400000}")
    private long jwtExpirationMs;

    /**
     * Gera um token JWT a partir da autenticação do usuário.
     *
     * @param authentication objeto de autenticação do Spring Security contendo o principal (username)
     * @return token JWT assinado
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        return generateTokenFromUsername(username);
    }

    /**
     * Gera um token JWT a partir do username do usuário.
     *
     * @param username nome de usuário
     * @return token JWT assinado
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        claims.put("iat", now.getTime());

        SecretKey key = getSigningKey();

        logger.debug("Gerando token JWT para usuário: {}", username);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrai o username do token JWT.
     *
     * @param token token JWT
     * @return username contido no token
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Erro ao extrair username do token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrai todas as claims do token JWT.
     *
     * @param token token JWT
     * @return claims do token
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtém a chave de assinatura do token.
     *
     * @return chave secreta para assinatura
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valida a integridade e expiracao de um token JWT.
     *
     * @param token token JWT a ser validado
     * @return true se o token eh valido, false caso contrario
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            logger.debug("Token JWT validado com sucesso");
            return true;
        } catch (SignatureException ex) {
            logger.error("Assinatura JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT malformado: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT não suportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("Claims JWT vazio: {}", ex.getMessage());
        } catch (JwtException ex) {
            logger.error("Erro ao validar token JWT: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Verifica se o token está expirado.
     *
     * @param token token JWT
     * @return true se o token está expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            logger.error("Erro ao verificar expiração do token: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Obtém a data de expiração do token.
     *
     * @param token token JWT
     * @return data de expiração
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Erro ao obter data de expiração: {}", e.getMessage());
            return null;
        }
    }
}

