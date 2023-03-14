package br.com.hub.cep.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE) //503
public class NotReadyException extends RuntimeException {
    public NotReadyException(String s) {
        super(s);
    }
}
