package br.com.hub.cep.model;

public enum Status {
    NEED_SETUP, // precisa baixar o CSV dos correios
    SETUP_RUNNING, // está baixando/salvando no banco
    READY, // o serviço está apto para ser consumido
}
