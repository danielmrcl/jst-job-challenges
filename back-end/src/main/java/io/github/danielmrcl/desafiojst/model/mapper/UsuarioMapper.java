package io.github.danielmrcl.desafiojst.model.mapper;

import io.github.danielmrcl.desafiojst.model.Usuario;
import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    Usuario toModel(UsuarioDTO usuarioDTO);
    UsuarioDTO toDTO(Usuario usuario);
}
