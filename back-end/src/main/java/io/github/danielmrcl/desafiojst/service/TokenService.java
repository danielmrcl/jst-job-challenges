package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    private final String KEY = "JstJobChallengesKey";

    private static final long expirationTime = 1800000;

    public String gerarToken(UsuarioDTO usuarioDTO) {
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(String.valueOf(usuarioDTO.getId()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();
    }

    public Claims pegarToken(String token) {
        return Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody();
    }

}
