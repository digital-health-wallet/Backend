package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Agendamento;

import java.util.List;

public interface GoogleCalendarRepository {
    /**
     * Busca eventos da agenda do Google e os converte
     * para o modelo de domínio Agendamento
     */
    List<Agendamento> buscarEventosDaAgenda(String accessToken);
}
