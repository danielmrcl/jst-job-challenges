package io.github.danielmrcl.desafiojst.utils.mapper;

import io.github.danielmrcl.desafiojst.model.Login;
import io.github.danielmrcl.desafiojst.model.dto.LoginDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoginMapper {
    LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

    Login toModel(LoginDTO loginDTO);
    LoginDTO toDTO(Login login);
}
