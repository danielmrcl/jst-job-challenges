package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.Login;
import io.github.danielmrcl.desafiojst.model.Mensagem;
import io.github.danielmrcl.desafiojst.model.dto.LoginDTO;
import io.github.danielmrcl.desafiojst.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @GetMapping
    public Mensagem logarUsuario(@RequestBody LoginDTO loginDTO) {
        return loginService.logarUsuario(loginDTO);
    }
}
