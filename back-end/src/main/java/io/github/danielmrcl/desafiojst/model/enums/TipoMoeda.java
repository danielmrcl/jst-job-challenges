package io.github.danielmrcl.desafiojst.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMoeda {
    REAL("R$"), DOLAR("US$"), EURO("€");

    private final String valor;
}
