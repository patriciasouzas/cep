package br.com.hub.cep.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Serviço está em instalação. Aguarde um momento.")
public class NotReadyException extends Exception{
}
