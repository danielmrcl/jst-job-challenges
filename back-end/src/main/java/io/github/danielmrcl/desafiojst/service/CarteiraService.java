package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.exception.GenericErrorException;
import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarteiraService {
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

        var saldoCarteira = carteiraUsuario.getSaldo();
        carteiraUsuario.setSaldo(saldoCarteira + valor);

        usuarioService.atualizarUsuarioSemVerificar(usuario);

        return new Mensagem(
                String.format("Valor %s%.2f depositado com sucesso", carteiraUsuario.getTipoMoeda().getValor(), valor)
        );
    }
}
