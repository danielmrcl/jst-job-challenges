package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import io.github.danielmrcl.desafiojst.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/deposito")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem depositarValor(@PathVariable(name = "id") long idUsuario,
                                   @RequestParam(name = "valor") float valor) {
        return carteiraService.depositarValor(idUsuario, valor);
    }

    @PutMapping("/estado")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem mudarEstadoAtivo(@PathVariable(name = "id") long idUsuario,
                                     @RequestParam(name = "ativo") boolean estadoAtivo) {
        return carteiraService.mudarEstadoAtivo(idUsuario, estadoAtivo);
    }

    @PutMapping("/cambio")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem trocarTipoMoeda(@PathVariable(name = "id") long idUsuario,
                                    @RequestParam(name = "para") TipoMoeda tipoPara) {
        return carteiraService.trocarTipoMoeda(idUsuario, tipoPara);
    }
}
