package io.github.danielmrcl.desafiojst.controller;

import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.github.danielmrcl.desafiojst.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioDTO procurarUsuarioPorId(@PathVariable long id) {
        return usuarioService.usuarioPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuarioPorId(@PathVariable long id,
                                    @RequestHeader(name = "Authorization") String token) {
        usuarioService.deletarUsuarioPorId(id, token);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO criarUsuario(@RequestBody @Valid UsuarioDTO usuarioDTO,
                                   @RequestHeader(name = "Password") String senhaUsuario) {
        return usuarioService.criarUsuario(usuarioDTO, senhaUsuario);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO atualizarUsuario(@PathVariable long id,
                                       @RequestHeader(name = "Authorization") String token,
                                       @RequestBody @Valid UsuarioDTO usuarioDTO,
                                       @RequestHeader(name = "Password", required = false) String senhaParaAtualizar) {
        return usuarioService.atualizarUsuario(id, usuarioDTO, senhaParaAtualizar, token);
    }
}
