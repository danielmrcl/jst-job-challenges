package io.github.danielmrcl.desafiojst.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private long id;

    @NotEmpty
    @Size(min = 2, max = 20)
    private String nome;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String sobrenome;

    @NotEmpty
    @Size(min = 5, max = 70)
    private String email;

    @NotEmpty
    @CPF
    private String cpf;

    @NotNull
    private LocalDate dataNasc;

    @NotNull
    private int carteiraId;
}
