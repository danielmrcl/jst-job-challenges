package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.apis.EmailApi;
import io.github.danielmrcl.desafiojst.exception.GenericErrorException;
import io.github.danielmrcl.desafiojst.exception.ObjectAlreadyExistsException;
import io.github.danielmrcl.desafiojst.exception.ObjectNotFoundException;
import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import io.github.danielmrcl.desafiojst.model.mapper.UsuarioMapper;
import io.github.danielmrcl.desafiojst.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private final String APIURLBASE = "https://api.eva.pingutil.com/email";

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
        verificarCpfUsuario(usuarioDTO.getCpf(), null);
        verificarEmailUsuario(usuarioDTO.getEmail(), null);

        var carteiraPadrao = CarteiraDTO.builder()
                .id(usuarioDTO.getId())
                .saldo(0)
                .tipoMoeda(TipoMoeda.BRL)
                .estadoAtivo(false)
                .build();
        usuarioDTO.setCarteira(carteiraPadrao);

        var usuarioParaSalvar = usuarioMapper.toModel(usuarioDTO);
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaSalvar));
    }

    public UsuarioDTO atualizarUsuario(long id, UsuarioDTO usuarioDTO) {
        var usuarioEncontrado = verificarIdUsuario(id);
        verificarCpfUsuario(usuarioDTO.getCpf(), id);
        verificarEmailUsuario(usuarioDTO.getEmail(), id);
        usuarioDTO.setId(id);

        var usuarioParaAtualizar = usuarioMapper.toModel(usuarioDTO);
        usuarioParaAtualizar.setCarteira(usuarioEncontrado.getCarteira());

        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaAtualizar));
    }

    public UsuarioDTO atualizarUsuarioSemVerificar(UsuarioDTO usuarioDTO) {
        var usuarioParaAtualizar = usuarioMapper.toModel(usuarioDTO);
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaAtualizar));
    }

    private Usuario verificarIdUsuario(long id) {
        var optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isEmpty()) {
            String message = String.format("Usuario ID %d: Não encontrado no banco de dados", id);
            throw new ObjectNotFoundException(message);
        }

        return optUsuario.get();
    }

    private void verificarCpfUsuario(String cpf, Long ignorarId) {
        var optUsuario = usuarioRepository.findByCpf(cpf);

        if (optUsuario.isPresent() && (ignorarId == null || optUsuario.get().getId() != ignorarId)) {
            String message = String.format("Usuario CPF %s: já existe no banco de dados", cpf);
            throw new ObjectAlreadyExistsException(message);
        }
    }

    private void verificarEmailUsuario(String email, Long ignorarId) {
        String url = String.format("%s?email=%s", APIURLBASE, email);

        RestTemplate restTemplate = new RestTemplate();
        var emailApi = restTemplate.getForObject(url, EmailApi.class);

        assert emailApi != null;
        boolean emailEntregavel = emailApi.getData().isDeliverable();
        boolean emailSpam = emailApi.getData().isSpam();

        if (!emailEntregavel || emailSpam) {
            String message = "O email informado não é valido, tente outro.";
            throw new GenericErrorException(message);
        }

        var optUsuario = usuarioRepository.findByEmail(email);

        if (optUsuario.isPresent() && (ignorarId == null || optUsuario.get().getId() != ignorarId)) {
            String message = String.format("Usuario EMAIL %s: já existe no banco de dados", email);
            throw new ObjectAlreadyExistsException(message);
        }
    }
}
