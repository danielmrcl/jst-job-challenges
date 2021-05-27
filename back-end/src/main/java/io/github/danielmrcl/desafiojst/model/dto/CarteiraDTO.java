package io.github.danielmrcl.desafiojst.model.dto;

import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarteiraDTO {
    private long id;

    @NotEmpty
    private double saldo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoMoeda tipoMoeda;

    @NotNull
    private boolean estadoAtivo;
}
