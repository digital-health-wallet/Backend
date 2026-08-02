package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;

/**
 * Resultado de operações que tentam, opcionalmente, sincronizar com o Google
 * Calendar (RF06). A sincronização nunca compromete a persistência local: se
 * falhar, o agendamento é mantido e o motivo da falha vai em avisoGoogle.
 */
public record ResultadoAgendamento(Agendamento agendamento, String avisoGoogle) {

    public static ResultadoAgendamento semAviso(Agendamento agendamento) {
        return new ResultadoAgendamento(agendamento, null);
    }
}
