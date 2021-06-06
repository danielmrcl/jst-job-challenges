package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Classe de serviço que implementa e logica de Token JWT
 *
 * @author Daniel Marcelo
 * @since v1.0
 */
@Service
public class TokenService {

    private final String KEY = "JstJobChallengesKey";
    private static final long expirationTime = 1800000;

    /**
     * Gera um Token JWT com o ID o usuário relacionado e um tempo limite de uso.
     *
     * @param usuarioDTO    Usuário pra relacionar ao token.
     * @return              Retorna o token gerado em forma de texto.
     */
    public String gerarToken(UsuarioDTO usuarioDTO) {
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(String.valueOf(usuarioDTO.getId()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();
    }

    /**
     * Decodificar um token com base no atributo chave.
     *
     * @param token     Token para ser decodificado.
     * @return          Retorna uma claims para o uso do token decodificado.
     */
    public Claims pegarToken(String token) {
        return Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody();
    }

}
