package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.apis.CambioApi;
import io.github.danielmrcl.desafiojst.exception.GenericErrorException;
import io.github.danielmrcl.desafiojst.model.dto.MensagemDTO;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Classe de serviço que implementa a lógica de Carteira.
 *
 * @author Daniel Marcelo
 * @since v0.2
 * @see io.github.danielmrcl.desafiojst.controller.CarteiraController
 */
@Service
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class CarteiraService {

    private UsuarioService usuarioService;
    private LoginService loginService;

    /**
     * Consulta informações de uma carteira de usuario.
     *
     * @param token     Token JWT para validação do usuario da carteira.
     * @return          Informações da carteira encontrada.
     */
    public CarteiraDTO infoCarteira(String token) {
        var usuario = loginService.usuarioPorToken(token);

        return usuario.getCarteira();
    }

    /**
     * Deposita um valor em uma carteira.
     *
     * @param valor     Valor à ser depositado.
     * @param token     Token JWT para validação do usuario da carteira.
     * @return          Mensagem de sucesso com o valor depositado.
     *
     * @throws GenericErrorException Lançado quando o atributo valor está inválido ou a carteira está desativada. Retorna mensagem de erro com código 400 (Bad Request)
     */
    public MensagemDTO depositarValor(float valor, String token) {
        if (valor < 1) {
            String message = String.format("O valor %.2f é muito baixo para ser depositado. Apenas valores entre 1 e 100", valor);
            throw new GenericErrorException(message);
        } else if (valor > 100) {
            String message = String.format("O valor %.2f é muito alto para ser depositado. Apenas valores entre 1 e 100", valor);
            throw new GenericErrorException(message);
        }

        var usuario = loginService.usuarioPorToken(token);
        var carteiraUsuario = usuario.getCarteira();

        if (!carteiraUsuario.isEstadoAtivo()) {
            String message = String.format("Esta carteira esta desativada", valor);
            throw new GenericErrorException(message);
        }

        var saldoCarteira = carteiraUsuario.getSaldo();
        carteiraUsuario.setSaldo(saldoCarteira + valor);

        usuarioService.atualizarUsuarioSemVerificar(usuario);

        return new MensagemDTO(
                String.format("Valor %s%.2f depositado com sucesso", carteiraUsuario.getTipoMoeda().getValor(), valor)
        );
    }

    /**
     * Muda o atributo estadoAtivo de uma carteira para Ativado ou Desativado.
     *
     * @param estadoAtivo   Novo estado da carteira (TRUE / FALSE).
     * @param token         Token JWT para validação do usuario da carteira.
     * @return              Mensagem de sucesso ao ativar.
     */
    public MensagemDTO mudarEstadoAtivo(boolean estadoAtivo, String token) {
        var usuario = loginService.usuarioPorToken(token);
        var carteiraUsuario = usuario.getCarteira();

        carteiraUsuario.setEstadoAtivo(estadoAtivo);

        usuarioService.atualizarUsuarioSemVerificar(usuario);

        return new MensagemDTO(String.format("Estado atualizado com sucesso!"));
    }

    /**
     * Realiza a função de câmbio de moeda utilizando a API CurrencyScoop. Atualiza os atributos: saldo e tipoMoeda.
     *
     * @param tipoPara  Tipo para o qual sera realizado o câmbio (BRL / USD / EUR).
     * @param token     Token JWT para validação do usuario da carteira.
     * @return          Mensagem de sucesso com informações do câmbio.
     */
    public MensagemDTO trocarTipoMoeda(TipoMoeda tipoPara, String token) {
        final String APIKEY = "ab01db841dcd32f4a495dfce24e1fa54";
        final String URLBASE = "https://api.currencyscoop.com/v1/convert";

        var usuario = loginService.usuarioPorToken(token);
        var carteiraUsuario = usuario.getCarteira();

        var carteiraUsuarioTipo = carteiraUsuario.getTipoMoeda();
        var carteiraUsuarioSaldo = carteiraUsuario.getSaldo();

        String url = String.format(
                "%s?from=%s&to=%s&amount=%s&api_key=%s",
                URLBASE,
                carteiraUsuarioTipo,
                tipoPara,
                carteiraUsuarioSaldo,
                APIKEY
        );

        RestTemplate restTemplate = new RestTemplate();
        var cambioApi = restTemplate.getForObject(url, CambioApi.class);

        assert cambioApi != null;
        var novoSaldo = cambioApi.getResponse().getValue();
        var novoTipo = cambioApi.getResponse().getTo();

        carteiraUsuario.setSaldo(novoSaldo);
        carteiraUsuario.setTipoMoeda(novoTipo);

        usuarioService.atualizarUsuarioSemVerificar(usuario);

        return new MensagemDTO(String.format(
                "Carteira atualizada com sucesso! | Tipo: %s -> %s | Saldo: %.2f -> %.2f",
                carteiraUsuarioTipo,
                novoTipo,
                carteiraUsuarioSaldo,
                novoSaldo
        ));
    }
}
