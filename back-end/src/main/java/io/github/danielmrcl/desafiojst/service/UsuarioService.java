package io.github.danielmrcl.desafiojst.service;

import io.github.danielmrcl.desafiojst.apis.EmailApi;
import io.github.danielmrcl.desafiojst.exception.GenericErrorException;
import io.github.danielmrcl.desafiojst.exception.InvalidTokenException;
import io.github.danielmrcl.desafiojst.exception.ObjectAlreadyExistsException;
import io.github.danielmrcl.desafiojst.exception.ObjectNotFoundException;
import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import io.github.danielmrcl.desafiojst.model.mapper.UsuarioMapper;
import io.github.danielmrcl.desafiojst.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe de serviço que implementa a lógica de Usuario.
 *
 * @author Daniel Marcelo
 * @since v0.1
 * @see io.github.danielmrcl.desafiojst.controller.UsuarioController
 */
@Service
public class UsuarioService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    private UsuarioMapper usuarioMapper = UsuarioMapper.INSTANCE;

    /**
     * Busca uma lista de usuários no banco de dados.
     *
     * @return  Retorna a lista de usuários encontrada.
     */
    public List<UsuarioDTO> listarUsuarios() {
        var usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica e busca um usuário no banco de dados com base em um ID.
     *
     * @param id    Atributo id para buscar na tabela Usuario.
     * @return      Retorna o usuário encontrado.
     */
    public UsuarioDTO usuarioPorId(long id) {
        var usuarioEncontrado = verificarIdUsuario(id);
        return usuarioMapper.toDTO(usuarioEncontrado);
    }

    /**
     * Busca um usuário no banco de dados com base em um Email.
     *
     * @param email     Atributo email para buscar na tabela Usuario
     * @return          Retorna o usuário encontrado.
     *
     * @throws ObjectNotFoundException  Lançado quando não é encontrado um usuário com o email informado. Retorna uma mensagem de erro com o código 404 (Not Found).
     */
    public UsuarioDTO usuarioPorEmail(String email) {
        var optUsuario = usuarioRepository.findByEmail(email);

        if (optUsuario.isEmpty()) {
            String message = String.format("Usuario EMAIL %s: Não encontrado no banco de dados", email);
            throw new ObjectNotFoundException(message);
        }

        return usuarioMapper.toDTO(optUsuario.get());
    }

    /**
     * Verifica e deleta um usuário e seus relacionamentos com base em um atributo ID.
     *
     * @param id        Atributo id para buscar na tabela de Usuario.
     * @param token     Token JWT para validar usuário.
     *
     * @throws InvalidTokenException    Lançado quando o token recebido não pertence ao id que será deletado. Retorna uma mensagem de erro com o código 401 (Unauthorized)
     */
    public void deletarUsuarioPorId(long id, String token) {
        var usuarioEncontrado = verificarIdUsuario(id);
        var usuarioToken = loginService.usuarioPorToken(token);

        if (usuarioToken.getId() != usuarioEncontrado.getId()) {
            throw new InvalidTokenException("Token inválido! Este Token não pertence a este Id de usuario");
        }

        loginService.deletarLoginPorUsuario(usuarioEncontrado);
    }

    /**
     * Valida e cria um usuário com Login e Carteira e salva no banco de dados.
     *
     * @param usuarioDTO        Usuário para inserir no banco de dados.
     * @param senhaParaSalvar   Senha para inserir na tabela Login.
     * @return                  Retorna o usuário criado.
     */
    public UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO, String senhaParaSalvar) {
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
        loginService.criarLogin(usuarioParaSalvar, senhaParaSalvar);

        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaSalvar));
    }

    /**
     * Valida e atualiza um usuário e seus atributos e relacionameno.
     *
     * @param id                    Atributo ID para realizar a busca no banco de dados.
     * @param usuarioDTO            Usuário com campos atualizados para salvar no banco.
     * @param senhaParaAtualizar    Senha atualizada para salvar no banco. NULL para não atualizar senha.
     * @param token                 Token JWT para validar o usuario.
     * @return                      Retorna o usuario atualizado.
     *
     * @see #atualizarUsuarioSemVerificar(UsuarioDTO) Atualiza um usuario sem as presentes validações.
     *
     * @throws InvalidTokenException    Lançado quando o Token passado não pertence à este ID de usuario.
     */
    public UsuarioDTO atualizarUsuario(long id, UsuarioDTO usuarioDTO, String senhaParaAtualizar, String token) {
        var usuarioEncontrado = verificarIdUsuario(id);
        var usuarioToken = loginService.usuarioPorToken(token);

        if (usuarioToken.getId() != usuarioEncontrado.getId()) {
            throw new InvalidTokenException("Token inválido! Este Token não pertence a este Id de usuario");
        }

        verificarCpfUsuario(usuarioDTO.getCpf(), id);
        verificarEmailUsuario(usuarioDTO.getEmail(), id);
        usuarioDTO.setId(id);

        var usuarioParaAtualizar = usuarioMapper.toModel(usuarioDTO);
        usuarioParaAtualizar.setCarteira(usuarioEncontrado.getCarteira());

        if (senhaParaAtualizar != null) {
            loginService.atualizarLogin(usuarioParaAtualizar, senhaParaAtualizar);
        }

        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaAtualizar));
    }

    /**
     * Atualiza um usuário diretamente no banco de dados. Sem realizar validações de campos.
     *
     * @param usuarioDTO    Usuário com campos atualizados para salvar no banco.
     * @return              Retorna o usuário atualizado.
     *
     * @see #atualizarUsuario(long, UsuarioDTO, String, String) Atualiza o usuário realiando algumas validações.
     */
    public UsuarioDTO atualizarUsuarioSemVerificar(UsuarioDTO usuarioDTO) {
        var usuarioParaAtualizar = usuarioMapper.toModel(usuarioDTO);
        return usuarioMapper.toDTO(usuarioRepository.save(usuarioParaAtualizar));
    }

    /**
     * Realiza a verificação de usuário baseado em um ID.
     *
     * @param id    Atributo ID para realizar a verificação.
     * @return      Retorna o usuário encontrado.
     *
     * @throws ObjectNotFoundException  Lançado quando não é encontrado um usuário com o ID informado. Retorna uma mensagem de erro com o código 404 (Not Found).
     */
    private Usuario verificarIdUsuario(long id) {
        var optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isEmpty()) {
            String message = String.format("Usuario ID %d: Não encontrado no banco de dados", id);
            throw new ObjectNotFoundException(message);
        }

        return optUsuario.get();
    }

    /**
     * Verifica se um CPF de usuário ja existe no banco de dados.
     *
     * @param cpf           CPF para realizar a verificação.
     * @param ignorarId     ID do usuário para ignorar na verificação do CPF. NULL para não ignorar nenhum CPF.
     *
     * @throws ObjectAlreadyExistsException  Lançado quando já existe um usuário no banco com o CPF informado. Retorna uma mensagem de erro com o código 409 (Conflict).
     */
    private void verificarCpfUsuario(String cpf, Long ignorarId) {
        var optUsuario = usuarioRepository.findByCpf(cpf);

        if (optUsuario.isPresent() && (ignorarId == null || optUsuario.get().getId() != ignorarId)) {
            String message = String.format("Usuario CPF %s: já existe no banco de dados", cpf);
            throw new ObjectAlreadyExistsException(message);
        }
    }

    /**
     * Valida um Email utilizando a api EVA e verifica se já existem no banco de dados.
     *
     * @param email         Email para realizar as validações.
     * @param ignorarId     ID do usuário para ignorar na verificação do Email. NULL para não ignorar nenhum Email.
     *
     * @throws GenericErrorException            Lançado quando o Email não passa nas validações da api. Retorna uma mensagem de erro com o código 400 (Bad Request).
     * @throws ObjectAlreadyExistsException     Lançado quanddo já existe um usuário no banco com o email informado. Retorna uma mensagem de erro com o código 409 (Conflict).
     */
    private void verificarEmailUsuario(String email, Long ignorarId) {
        final String APIURLBASE = "https://api.eva.pingutil.com/email";

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
