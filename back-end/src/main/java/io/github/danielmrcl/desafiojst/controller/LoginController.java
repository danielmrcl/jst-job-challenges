package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.dto.MensagemDTO;
import io.github.danielmrcl.desafiojst.model.dto.LoginDTO;
import io.github.danielmrcl.desafiojst.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping
    public MensagemDTO logarUsuario(@RequestBody LoginDTO loginDTO) {
        return loginService.logarUsuario(loginDTO);
    }
}
