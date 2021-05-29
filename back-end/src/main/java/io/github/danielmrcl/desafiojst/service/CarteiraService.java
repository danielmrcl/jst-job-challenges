package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.apis.CambioApi;
import io.github.danielmrcl.desafiojst.exception.GenericErrorException;
import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CarteiraService {
    private final String APIKEY = "ab01db841dcd32f4a495dfce24e1fa54";
    private final String URLBASE = "https://api.currencyscoop.com/v1/convert";

    @Autowired
    private UsuarioService usuarioService;

    public CarteiraDTO infoCarteira(long idUsuario) {
        return usuarioService.usuarioPorId(idUsuario).getCarteira();
    }

    public Mensagem depositarValor(long idUsuario, float valor) {
        if (valor < 1) {
            String message = String.format("O valor %.2f é muito baixo para ser depositado. Apenas valores entre 1 e 100", valor);
            throw new GenericErrorException(message);
        } else if (valor > 100) {
            String message = String.format("O valor %.2f é muito alto para ser depositado. Apenas valores entre 1 e 100", valor);
            throw new GenericErrorException(message);
        }

        var usuario = usuarioService.usuarioPorId(idUsuario);
        var carteiraUsuario = usuario.getCarteira();

        if (!carteiraUsuario.isEstadoAtivo()) {
            String message = String.format("Esta carteira esta desativada", valor);
            throw new GenericErrorException(message);
        }

        var saldoCarteira = carteiraUsuario.getSaldo();
        carteiraUsuario.setSaldo(saldoCarteira + valor);

        usuarioService.atualizarUsuarioSemVerificar(usuario);

        return new Mensagem(
                String.format("Valor %s%.2f depositado com sucesso", carteiraUsuario.getTipoMoeda().getValor(), valor)
        );
    }

    public Mensagem mudarEstadoAtivo(long idUsuario, boolean estadoAtivo) {
        var usuario = usuarioService.usuarioPorId(idUsuario);
        var carteiraUsuario = usuario.getCarteira();

        carteiraUsuario.setEstadoAtivo(estadoAtivo);

        usuarioService.atualizarUsuarioSemVerificar(usuario);

        return new Mensagem(String.format("Estado atualizado com sucesso!"));
    }

    public Mensagem trocarTipoMoeda(long idUsuario, TipoMoeda tipoPara) {
        var usuario = usuarioService.usuarioPorId(idUsuario);
        var carteiraUsuario = usuario.getCarteira();

        var carteiraUsuarioTipo = carteiraUsuario.getTipoMoeda();
        var carteiraUsuarioSaldo = carteiraUsuario.getSaldo();

        String url = String.format(
                "%s?from=%s&to=%s&amount=%f&api_key=%s",
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

        return new Mensagem(String.format(
                "Carteira atualizada com sucesso! | Tipo: %s -> %s | Saldo: %.2f -> %.2f",
                carteiraUsuarioTipo,
                novoTipo,
                carteiraUsuarioSaldo,
                novoSaldo
        ));
    }
}
