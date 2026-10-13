package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Agendamento;

import java.util.List;

public interface GoogleCalendarRepository {
    List<Agendamento> buscarEventosDaAgenda(String accessToken);
    String criarCalendario(String accessToken, String nomeCalendario);
    String criarEvento(String accessToken, String calendarId, Agendamento agendamento);
    void atualizarEvento(String accessToken, String calendarId, String googleEventId, Agendamento agendamento);
    void removerEvento(String accessToken, String calendarId, String googleEventId);
}
