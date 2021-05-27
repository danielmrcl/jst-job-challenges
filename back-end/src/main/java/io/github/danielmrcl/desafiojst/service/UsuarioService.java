package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.apis.EmailApi;
import io.github.danielmrcl.desafiojst.apis.exceptions.InvalidEmailException;
import io.github.danielmrcl.desafiojst.exception.ObjectAlreadyExistsException;
import io.github.danielmrcl.desafiojst.exception.ObjectNotFoundException;
import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.github.danielmrcl.desafiojst.model.mapper.UsuarioMapper;
import io.github.danielmrcl.desafiojst.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioMapper usuarioMapper = UsuarioMapper.INSTANCE;

    public List<UsuarioDTO> listarUsuarios() {
        var usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO usuarioPorId(long id) {
        var usuarioEncontrado = verificarIdUsuario(id);
        return usuarioMapper.toDTO(usuarioEncontrado);
    }

    public void deletarUsuarioPorId(long id) {
        var usuarioEncontrado = verificarIdUsuario(id);
        usuarioRepository.delete(usuarioEncontrado);
    }

    public UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO) {
        verificarCpfUsuario(usuarioDTO.getCpf());
        verificarEmailUsuario(usuarioDTO.getEmail());

        var usuarioParaSalvar = usuarioMapper.toModel(usuarioDTO);

        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaSalvar));
    }

    public UsuarioDTO atualizarUsuario(long id, UsuarioDTO usuarioDTO) {
        verificarIdUsuario(id);
        verificarCpfUsuario(usuarioDTO.getCpf());
        verificarEmailUsuario(usuarioDTO.getEmail());
        usuarioDTO.setId(id);

        var usuarioParaAtualizar = usuarioMapper.toModel(usuarioDTO);
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaAtualizar));
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
        String url = String.format("https://api.eva.pingutil.com/email?email=%s", email);

        RestTemplate restTemplate = new RestTemplate();
        var emailApi = restTemplate.getForObject(url, EmailApi.class);

        boolean emailEntregavel = emailApi.getData().isDeliverable();
        boolean emailSpam = emailApi.getData().isSpam();

        if (!emailEntregavel || emailSpam) {
            String message = "O email informado não é valido, tente outro.";
            throw new InvalidEmailException(message);
        }

        var optUsuario = usuarioRepository.findByEmail(email);

        if (optUsuario.isPresent()) {
            String message = String.format("Usuario EMAIL %s: já existe no banco de dados", email);
            throw new ObjectAlreadyExistsException(message);
        }
    }
}
