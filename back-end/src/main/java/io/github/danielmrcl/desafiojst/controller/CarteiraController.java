package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/api/usuarios/{id}/carteira")
public class CarteiraController {
    @Autowired
    private CarteiraService carteiraService;

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public CarteiraDTO infoCarteira(@PathVariable(name = "id") long idUsuario) {
        return carteiraService.infoCarteira(idUsuario);
    }

    @PutMapping("/depositar")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem depositarValor(@PathVariable(name = "id") long idUsuario, @PathParam("valor") float valor) {
        return carteiraService.depositarValor(idUsuario, valor);
    }
}
