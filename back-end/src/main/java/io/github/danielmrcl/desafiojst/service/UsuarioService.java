package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.exception.ObjectAlreadyExistsException;
import io.github.danielmrcl.desafiojst.exception.ObjectNotFoundException;
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

    public Usuario usuarioPorId(long id) {
        var usuarioEncontrado = verificarIdUsuario(id);
        return usuarioEncontrado;
    }

    public void deletarUsuarioPorId(long id) {
        var usuarioEncontrado = verificarIdUsuario(id);
        usuarioRepository.delete(usuarioEncontrado);
    }

    public Usuario criarUsuario(Usuario usuario) {
        verificarCpfUsuario(usuario.getCpf());
        verificarEmailUsuario(usuario.getEmail());

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(long id, Usuario usuario) {
        verificarIdUsuario(id);
        verificarCpfUsuario(usuario.getCpf());
        verificarEmailUsuario(usuario.getEmail());

        usuario.setId(id);
        return usuarioRepository.save(usuario);
    }

    public Usuario verificarIdUsuario(long id) {
        var optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isEmpty()) {
            String message = String.format("Usuario ID %d: Não encontrado no banco de dados", id);
            throw new ObjectNotFoundException(message);
        }

        return optUsuario.get();
    }

    private void verificarCpfUsuario(String cpf) {
        var optUsuario = usuarioRepository.findByCpf(cpf);

        if (optUsuario.isPresent()) {
            String message = String.format("Usuario CPF %s: já existe no banco de dados", cpf);
            throw new ObjectAlreadyExistsException(message);
        }
    }

    private void verificarEmailUsuario(String email) {
        var optUsuario = usuarioRepository.findByEmail(email);

        if (optUsuario.isPresent()) {
            String message = String.format("Usuario EMAIL %s: já existe no banco de dados", email);
            throw new ObjectAlreadyExistsException(message);
        }
    }
}
