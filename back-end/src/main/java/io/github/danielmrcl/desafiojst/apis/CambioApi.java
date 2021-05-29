package io.github.danielmrcl.desafiojst.apis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CambioApi {
    private CambioApiMeta meta;
    private CambioApiResponse response;
}
