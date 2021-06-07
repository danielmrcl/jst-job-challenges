package io.github.danielmrcl.desafiojst.utils.builder;

import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.dto.UsuarioDTO;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public class UsuarioDTOBuilder {
    @Builder.Default
    private long id = 1L;

    @Builder.Default
    private String nome = "String";

    @Builder.Default
    private String sobrenome = "String";

    @Builder.Default
    private String email = "emailteste@mail.com";

    @Builder.Default
    private String cpf = "29198953036";

    @Builder.Default
    private LocalDate dataNasc = LocalDate.of(1970, 01, 01);

    @Builder.Default
    private CarteiraDTO carteira = CarteiraDTOBuilder.builder().build().toCarteiraDTO();

    public UsuarioDTO toUsuarioDTO() {
        return new UsuarioDTO(
                id,
                nome,
                sobrenome,
                email,
                cpf,
                dataNasc,
                carteira
        );
    }
}
