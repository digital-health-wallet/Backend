package br.com.healthwallet.domain.exception;

public class ConflitoAgendamentoException extends RuntimeException {

    public ConflitoAgendamentoException(String mensagem) {
        super(mensagem);
    }
}
