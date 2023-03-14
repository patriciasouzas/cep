package br.com.hub.cep.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT) //204
public class NoContentException extends RuntimeException {
    public NoContentException(String s) {
        super(s);
    }
}
