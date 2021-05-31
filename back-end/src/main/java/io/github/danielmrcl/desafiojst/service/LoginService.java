package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.exception.LoginErrorException;
import io.github.danielmrcl.desafiojst.model.Login;
import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Mensagem logarUsuario(String email, String senha, HttpSession session) {
        var usuarioEncontrado = usuarioService.usuarioPorEmail(email);
        var loginEncontrado = loginRepository.getByUserId(usuarioEncontrado.getId()).get();

        if (!codificarSenha(senha).equals(loginEncontrado.getSenha())) {
            throw new LoginErrorException("Senha incorreta");
        }

        session.setAttribute("idUsuarioLogado", usuarioEncontrado.getId());

        return new Mensagem(String.format("Usuario ID %d: Logado com sucesso", usuarioEncontrado.getId()));
    }

    public void criarLogin(Usuario usuarioParaSalvar, String senhaParaSalvar) {
        loginRepository.save(
                Login.builder()
                        .usuario(usuarioParaSalvar)
                        .senha(codificarSenha(senhaParaSalvar))
                        .build()
        );
    }

    public void atualizarLogin(Usuario usuarioParaAtualizar, String senhaParaAtualizar) {
        var login = loginRepository.getByUserId(usuarioParaAtualizar.getId()).get();

        login.setUsuario(usuarioParaAtualizar);
        login.setSenha(codificarSenha(senhaParaAtualizar));

        loginRepository.save(login);
    }

    private String codificarSenha(String senhaParaSalvar) {
        return Base64.getEncoder()
                .encodeToString(senhaParaSalvar.getBytes(StandardCharsets.UTF_8));
    }
}
