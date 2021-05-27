package io.github.danielmrcl.desafiojst.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMoeda {
    REAL("BRL"),
    DOLAR("USD"),
    EURO("EUR"),
    BITCOIN("BTC");

    private final String valor;
}
