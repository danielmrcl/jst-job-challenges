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

    public Usuario usuarioPorId(long id) throws ObjectNotFoundException {
        var optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isEmpty()) {
            String message = String.format("Usuario ID %d: Não encontrado no banco de dados", id);
            throw new ObjectNotFoundException(message);
        }

        return optUsuario.get();
    }

    public Usuario criarUsuario(Usuario usuario) {
        verificarCpfUsuario(usuario.getCpf());
        verificarEmailUsuario(usuario.getEmail());

        return usuarioRepository.save(usuario);
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
