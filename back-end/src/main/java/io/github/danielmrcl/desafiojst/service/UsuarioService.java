package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
