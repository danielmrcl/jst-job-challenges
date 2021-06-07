package io.github.danielmrcl.desafiojst.utils.builder;

import io.github.danielmrcl.desafiojst.model.dto.CarteiraDTO;
import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import lombok.Builder;

@Builder
public class CarteiraDTOBuilder {
    @Builder.Default
    private long id = 1L;

    @Builder.Default
    private double saldo = 10;

    @Builder.Default
    private TipoMoeda tipoMoeda = TipoMoeda.BRL;

    @Builder.Default
    private boolean estadoAtivo = true;

    public CarteiraDTO toCarteiraDTO() {
        return new CarteiraDTO(
                id,
                saldo,
                tipoMoeda,
                estadoAtivo
        );
    }
}
