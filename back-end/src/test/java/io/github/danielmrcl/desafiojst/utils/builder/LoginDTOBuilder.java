package io.github.danielmrcl.desafiojst.utils.builder;

import io.github.danielmrcl.desafiojst.model.dto.LoginDTO;
import lombok.Builder;

@Builder
public class LoginDTOBuilder {
    @Builder.Default
    private String email = "emailteste@mail.com";

    @Builder.Default
    private String senha = "senhateste";

    public LoginDTO toLoginDTO() {
        return new LoginDTO(
                email,
                senha
        );
    }
}
