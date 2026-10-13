package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;


public record ResultadoAgendamento(Agendamento agendamento, String avisoGoogle) {

    public static ResultadoAgendamento semAviso(Agendamento agendamento) {
        return new ResultadoAgendamento(agendamento, null);
    }
}
