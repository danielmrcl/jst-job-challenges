package io.github.danielmrcl.desafiojst.model;

import io.github.danielmrcl.desafiojst.model.enums.TipoMoeda;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "carteira")
public class Carteira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "saldo", nullable = false)
    private double saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_moeda", nullable = false)
    private TipoMoeda tipoMoeda;

    @Column(name = "estado_ativo", nullable = false)
    private boolean estadoAtivo;
}
