package io.github.danielmrcl.desafiojst.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMoeda {
    BRL("R$"), USD("US$"), EUR("€");

    private final String valor;
}
