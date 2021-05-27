package io.github.danielmrcl.desafiojst.apis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EmailApiDados {
    private String email_address;
    private String domain;
    private boolean valid_syntax;
    private boolean disposable;
    private boolean webmail;
    private boolean deliverable;
    private boolean catch_all;
    private boolean gibberish;
    private boolean spam;
}
