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
    public CarteiraDTO infoCarteira(HttpSession session) {
        return carteiraService.infoCarteira(session);
    }

    @PostMapping("/deposito")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem depositarValor(@RequestParam(name = "valor") float valor,
                                   HttpSession session) {
        return carteiraService.depositarValor(valor, session);
    }

    @PutMapping("/estado")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem mudarEstadoAtivo(@RequestParam(name = "ativo") boolean estadoAtivo,
                                     HttpSession session) {
        return carteiraService.mudarEstadoAtivo(estadoAtivo, session);
    }

    @PutMapping("/cambio")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem trocarTipoMoeda(@RequestParam(name = "para") TipoMoeda tipoPara,
                                    HttpSession session) {
        return carteiraService.trocarTipoMoeda(tipoPara, session);
    }
}
