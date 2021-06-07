package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.model.dto.MensagemDTO;
import io.github.danielmrcl.desafiojst.utils.builder.LoginDTOBuilder;
import io.github.danielmrcl.desafiojst.utils.builder.UsuarioDTOBuilder;
import io.github.danielmrcl.desafiojst.utils.mapper.LoginMapper;
import io.github.danielmrcl.desafiojst.model.Login;
import io.github.danielmrcl.desafiojst.exception.LoginErrorException;
import io.github.danielmrcl.desafiojst.exception.InvalidTokenException;
import io.github.danielmrcl.desafiojst.repository.LoginRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    private final LoginMapper loginMapper = LoginMapper.INSTANCE;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginService loginService;

    @Test
    void testLogarUsuarioQuandoSenhaCorretaEntaoRetorneMensagemComToken() {
        /* given */
        var loginDTO = LoginDTOBuilder.builder().build().toLoginDTO();
        var usuarioDTO = UsuarioDTOBuilder.builder().build().toUsuarioDTO();

        Login loginEncontrado = loginMapper.toModel(loginDTO);
        // injetando uma senha correta no loginDTO:
        loginEncontrado.setSenha(loginService.codificarSenha(loginDTO.getSenha()));

        String tokenTeste = "token";

        /* when */
        Mockito.when(usuarioService.usuarioPorEmail(loginDTO.getEmail()))
                .thenReturn(usuarioDTO);
        Mockito.when(loginRepository.getByUserId(usuarioDTO.getId()))
                .thenReturn(Optional.of(loginEncontrado));
        Mockito.when(tokenService.gerarToken(usuarioDTO))
                .thenReturn(tokenTeste);

        /* then */
        var retorno = loginService.logarUsuario(loginDTO);

        /* asserts */
        var classeEsperada = new MensagemDTO(tokenTeste);

        Assertions.assertTrue(retorno.getClass() == classeEsperada.getClass());
        Assertions.assertTrue(retorno.getMsg().equals(classeEsperada.getMsg()));
    }

    @Test
    void testLogarUsuarioQuandoSenhaIncorretaEntaoRetorneLoginErrorexception() {
        /* given */
        var loginDTO = LoginDTOBuilder.builder().build().toLoginDTO();
        var usuarioDTO = UsuarioDTOBuilder.builder().build().toUsuarioDTO();

        Login loginEncontrado = loginMapper.toModel(loginDTO);
        // injetando uma senha incorreta no loginDTO:
        loginEncontrado.setSenha(loginService.codificarSenha("outrasenha"));

        /* when */
        Mockito.when(usuarioService.usuarioPorEmail(loginDTO.getEmail()))
                .thenReturn(usuarioDTO);
        Mockito.when(loginRepository.getByUserId(usuarioDTO.getId()))
                .thenReturn(Optional.of(loginEncontrado));

        /* then asserts */
        RuntimeException exception = Assertions.assertThrows(LoginErrorException.class, () -> {
            loginService.logarUsuario(loginDTO);
        });

        String mensagemEsperada = "Senha incorreta";
        String mensagemEncontrada = exception.getMessage();

        Assertions.assertTrue(mensagemEsperada.equals(mensagemEncontrada));
    }

    @Test
    void testUsuarioPorTokenQuandoTokenValidoEntaoRetorneUsuarioDTO() {
        /* given */
        var loginDTO = LoginDTOBuilder.builder().build().toLoginDTO();
        var usuarioDTO = UsuarioDTOBuilder.builder().build().toUsuarioDTO();

        var login = loginMapper.toModel(loginDTO);
        var SecretKey = "SecretKEY";

        // gerando token válido
        String token = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(String.valueOf(usuarioDTO.getId()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(SignatureAlgorithm.HS256, SecretKey)
                .compact();

        login.setTokenAcesso(token);

        /* when */
        Mockito.when(loginRepository.getByUserId(usuarioDTO.getId()))
                .thenReturn(Optional.of(login));
        Mockito.when(usuarioService.usuarioPorId(usuarioDTO.getId()))
                .thenReturn(usuarioDTO);
        Mockito.when(tokenService.pegarToken(token))
                .thenReturn(Jwts.parser()
                        .setSigningKey(SecretKey)
                        .parseClaimsJws(token)
                        .getBody());

        /* then */
        var retorno = loginService.usuarioPorToken(token);

        /* assert */
        Assertions.assertTrue(usuarioDTO.getClass() == retorno.getClass());
        Assertions.assertTrue(retorno.getId() == usuarioDTO.getId());
    }

    @Test
    void testUsuarioPorTokenQuandoTokenDiferenteEntaoRetorneInvalidTokenException() {
        /* given */
        var loginDTO = LoginDTOBuilder.builder().build().toLoginDTO();
        var usuarioDTO = UsuarioDTOBuilder.builder().build().toUsuarioDTO();

        var login = loginMapper.toModel(loginDTO);
        var SecretKey = "SecretKEY";

        // gerando token válido
        String token = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(String.valueOf(usuarioDTO.getId()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(SignatureAlgorithm.HS256, SecretKey)
                .compact();

        // gerando token diferente para o login
        login.setTokenAcesso(
                Jwts.builder()
                    .setIssuedAt(new Date(System.currentTimeMillis() - 1800000))
                    .setSubject(String.valueOf(usuarioDTO.getId()))
                    .setExpiration(new Date(System.currentTimeMillis()))
                    .signWith(SignatureAlgorithm.HS256, SecretKey)
                    .compact()
        );

        /* when */
        Mockito.when(loginRepository.getByUserId(usuarioDTO.getId()))
                .thenReturn(Optional.of(login));
        Mockito.when(tokenService.pegarToken(token))
                .thenReturn(Jwts.parser()
                        .setSigningKey(SecretKey)
                        .parseClaimsJws(token)
                        .getBody());

        /* then asserts */
        RuntimeException exception = Assertions.assertThrows(InvalidTokenException.class, () -> {
            loginService.usuarioPorToken(token);
        });

        String mensagemEsperada = "Token inválido! Realize o Login para gerar um novo";
        String mensagemEncontrada = exception.getMessage();

        Assertions.assertTrue(mensagemEsperada.equals(mensagemEncontrada));
    }
}
