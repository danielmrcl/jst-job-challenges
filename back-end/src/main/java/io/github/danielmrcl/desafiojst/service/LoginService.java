package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.exception.InvalidTokenException;
import io.github.danielmrcl.desafiojst.exception.LoginErrorException;
import io.github.danielmrcl.desafiojst.exception.ObjectNotFoundException;
import io.github.danielmrcl.desafiojst.model.Login;
import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.model.dto.LoginDTO;
import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.github.danielmrcl.desafiojst.repository.LoginRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private TokenService tokenService;

    public Mensagem logarUsuario(LoginDTO loginDTO) {
        var usuarioEncontrado = usuarioService.usuarioPorEmail(loginDTO.getEmail());
        var loginEncontrado = loginRepository.getByUserId(usuarioEncontrado.getId()).get();

        if (!codificarSenha(loginDTO.getSenha()).equals(loginEncontrado.getSenha())) {
            throw new LoginErrorException("Senha incorreta");
        }

        loginEncontrado.setTokenAcesso(tokenService.gerarToken(usuarioEncontrado));
        loginRepository.save(loginEncontrado);

        return new Mensagem(loginEncontrado.getTokenAcesso());
    }

    public void criarLogin(Usuario usuarioParaSalvar, String senhaParaSalvar) {
        loginRepository.save(
                Login.builder()
                        .usuario(usuarioParaSalvar)
                        .senha(codificarSenha(senhaParaSalvar))
                        .tokenAcesso(null)
                        .build()
        );
    }

    public void atualizarLogin(Usuario usuarioParaAtualizar, String senhaParaAtualizar) {
        var login = loginRepository.getByUserId(usuarioParaAtualizar.getId()).get();

        login.setUsuario(usuarioParaAtualizar);
        login.setSenha(codificarSenha(senhaParaAtualizar));

        loginRepository.save(login);
    }

    public UsuarioDTO usuarioPorToken(String token) {
        Claims claim = null;

        try {
            claim = tokenService.pegarToken(token);
        } catch (Exception e) {
            throw new InvalidTokenException("Token inválido! Realize o Login para gerar um novo");
        }

        var idUsuario = Long.parseLong(claim.getSubject());
        var loginUsuario = loginRepository.getByUserId(idUsuario).get();

        if (!token.equals(loginUsuario.getTokenAcesso())) {
            throw new InvalidTokenException("Token inválido! Realize o Login para gerar um novo");
        } else if (claim.getExpiration().before(new Date(System.currentTimeMillis()))) {
            throw new InvalidTokenException("Token expirado! Realize o Login novamente para gerar um novo");
        }

        return usuarioService.usuarioPorId(idUsuario);
    }

    public void deletarLoginPorUsuario(Usuario usuario) {
        var loginParaDeletar = loginRepository.getByUserId(usuario.getId()).get();
        loginRepository.delete(loginParaDeletar);
    }

    private String codificarSenha(String senhaParaSalvar) {
        return Base64.getEncoder()
                .encodeToString(senhaParaSalvar.getBytes(StandardCharsets.UTF_8));
    }
}
