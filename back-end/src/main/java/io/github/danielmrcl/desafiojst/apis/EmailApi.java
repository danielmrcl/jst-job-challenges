package io.github.danielmrcl.desafiojst.apis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EmailApi {
    private String status;
    private EmailApiDados data;
}
