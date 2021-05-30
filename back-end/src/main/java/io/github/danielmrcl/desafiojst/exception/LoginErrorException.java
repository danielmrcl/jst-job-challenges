package io.github.danielmrcl.desafiojst.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class LoginErrorException extends RuntimeException {
    public LoginErrorException(String mensagem) {
        super(mensagem);
    }
}
