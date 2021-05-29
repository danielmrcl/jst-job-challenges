package io.github.danielmrcl.desafiojst.apis;

import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CambioApiResponse {
    private String timestamp;
    private String date;
    private TipoMoeda from;
    private TipoMoeda to;
    private int amounth;
    private double value;
}
