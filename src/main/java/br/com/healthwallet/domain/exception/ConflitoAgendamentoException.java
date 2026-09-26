package br.com.healthwallet.domain.exception;

/**
 * UC03 - Fluxo de Exceção: já existe consulta para o mesmo paciente no horário
 * informado. Bloqueia o cadastro em vez de permitir duas consultas sobrepostas.
 */
public class ConflitoAgendamentoException extends RuntimeException {

    public ConflitoAgendamentoException(String mensagem) {
        super(mensagem);
    }
}
