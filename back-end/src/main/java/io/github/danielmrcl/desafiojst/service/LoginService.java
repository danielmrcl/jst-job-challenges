package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.exception.InvalidTokenException;
import io.github.danielmrcl.desafiojst.exception.LoginErrorException;
import io.github.danielmrcl.desafiojst.model.Login;
import io.github.danielmrcl.desafiojst.model.dto.MensagemDTO;
import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.model.dto.LoginDTO;
import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.github.danielmrcl.desafiojst.repository.LoginRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * Classe de serviço que implementa a lógica de Login
 *
 * @author Daniel Marcelo
 * @since v1.0
 * @see io.github.danielmrcl.desafiojst.controller.LoginController
 */
@Service
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class LoginService {

    private LoginRepository loginRepository;
    private UsuarioService usuarioService;
    private TokenService tokenService;

    /**
     * Realiza as verificações de login passado por parâmetro e compara com o banco de dados.
     *
     * @param loginDTO  Login com email e senha para realizar a verificação.
     * @return          Mensagem com um token de acesso.
     *
     * @throws LoginErrorException   Lançado quando a senha está incorreta. Retorna mensagem de erro com código 401 (Unauthorized).
     */
    public MensagemDTO logarUsuario(LoginDTO loginDTO) {
        var usuarioEncontrado = usuarioService.usuarioPorEmail(loginDTO.getEmail());
        var talvezLoginEncontrado = loginRepository.getByUserId(usuarioEncontrado.getId());

        var loginEncontrado = talvezLoginEncontrado.isPresent() ? talvezLoginEncontrado.get() : null;

        if (!codificarSenha(loginDTO.getSenha()).equals(loginEncontrado.getSenha())) {
            throw new LoginErrorException("Senha incorreta");
        }

        loginEncontrado.setTokenAcesso(tokenService.gerarToken(usuarioEncontrado));
        loginRepository.save(loginEncontrado);

        return new MensagemDTO(loginEncontrado.getTokenAcesso());
    }

    /**
     * Cria uma instância de Login com os valores dos atributos e salva no banco de dados com senha codificada.
     *
     * @param usuarioParaSalvar     Usuário relacionado à este login.
     * @param senhaParaSalvar       Senha que será salva no banco de dados.
     */
    public void criarLogin(Usuario usuarioParaSalvar, String senhaParaSalvar) {
        loginRepository.save(
                Login.builder()
                        .usuario(usuarioParaSalvar)
                        .senha(codificarSenha(senhaParaSalvar))
                        .tokenAcesso(null)
                        .build()
        );
    }

    /**
     * Atualiza campos de um login com base no usuário relacionado.
     *
     * @param usuarioParaAtualizar      Usuario ao qual será atualizado.
     * @param senhaParaAtualizar        Senha para atualizar no banco de dados.
     */
    public void atualizarLogin(Usuario usuarioParaAtualizar, String senhaParaAtualizar) {
        var login = loginRepository.getByUserId(usuarioParaAtualizar.getId()).get();

        login.setUsuario(usuarioParaAtualizar);
        login.setSenha(codificarSenha(senhaParaAtualizar));

        loginRepository.save(login);
    }

    /**
     * Realiza a validação de um Token JWT passado por parâmetro.
     *
     * @param token     Token JWT que será validado.
     * @return          Usuário relacionado ao token.
     *
     * @throws InvalidTokenException    Lançado quando o token é inválido ou é diferente do token encontrado no banco de dados. Retorna código de erro 401 (Unauthorized).
     */
    public UsuarioDTO usuarioPorToken(String token) {
        Claims claim = null;

        try {
            claim = tokenService.pegarToken(token);
        } catch (ExpiredJwtException exjwt) {
            throw new InvalidTokenException("Token expirado! Realize o Login novamente para gerar um novo");
        } catch (Exception ex) {
            throw new InvalidTokenException("Token inválido! Realize o Login para gerar um novo");
        }

        var idUsuario = Long.parseLong(claim.getSubject());
        var talvezLoginUsuario = loginRepository.getByUserId(idUsuario);
        var loginUsuario = talvezLoginUsuario.orElse(null);

        if (!token.equals(loginUsuario.getTokenAcesso())) {
            throw new InvalidTokenException("Token inválido! Realize o Login para gerar um novo");
        }

        return usuarioService.usuarioPorId(idUsuario);
    }

    /**
     * Remove um login e todos os seus campos de modo recursivo.
     *
     * @param usuario   Usuário relacionado ao login que será deletado.
     */
    public void deletarLoginPorUsuario(Usuario usuario) {
        var loginParaDeletar = loginRepository.getByUserId(usuario.getId()).get();
        loginRepository.delete(loginParaDeletar);
    }

    /**
     * Codifica uma senha com o método Base64.
     *
     * @param senhaParaCodificar    Senha em texto que será codificada.
     * @return                      Retorna a senha em código Base64.
     */
    protected String codificarSenha(String senhaParaCodificar) {
        return Base64.getEncoder()
                .encodeToString(senhaParaCodificar.getBytes(StandardCharsets.UTF_8));
    }
}
