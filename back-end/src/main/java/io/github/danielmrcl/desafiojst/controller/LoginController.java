package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.Mensagem;
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
    public Mensagem logarUsuario(@RequestHeader(name = "email") String email,
                                 @RequestHeader(name = "password") String senha, HttpSession session) {
        return loginService.logarUsuario(email, senha, session);
    }
}
