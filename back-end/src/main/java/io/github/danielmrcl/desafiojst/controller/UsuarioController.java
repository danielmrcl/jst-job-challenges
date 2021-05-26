package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.exception.ObjectNotFoundException;
import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Usuario procurarUsuarioPorId(@PathVariable long id) throws ObjectNotFoundException {
        return usuarioService.usuarioPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuarioPorId(@PathVariable long id) {
        usuarioService.deletarUsuarioPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario criarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.criarUsuario(usuario);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario atualizarUsuario(@PathVariable long id, @RequestBody Usuario usuario) {
        return usuarioService.atualizarUsuario(id, usuario);
    }
}
