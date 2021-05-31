package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import io.github.danielmrcl.desafiojst.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/carteira")
public class CarteiraController {
    @Autowired
    private CarteiraService carteiraService;

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public CarteiraDTO infoCarteira(@RequestHeader(name = "Authorization") String token) {
        return carteiraService.infoCarteira(token);
    }

    @PostMapping("/deposito")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem depositarValor(@RequestParam(name = "valor") float valor,
                                   @RequestHeader(name = "Authorization") String token) {
        return carteiraService.depositarValor(valor, token);
    }

    @PutMapping("/estado")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem mudarEstadoAtivo(@RequestParam(name = "ativo") boolean estadoAtivo,
                                     @RequestHeader(name = "Authorization") String token) {
        return carteiraService.mudarEstadoAtivo(estadoAtivo, token);
    }

    @PutMapping("/cambio")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem trocarTipoMoeda(@RequestParam(name = "para") TipoMoeda tipoPara,
                                    @RequestHeader(name = "Authorization") String token) {
        return carteiraService.trocarTipoMoeda(tipoPara, token);
    }
}
