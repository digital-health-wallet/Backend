package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Agendamento;

import java.util.List;

public interface GoogleCalendarRepository {
    /**
     * Busca eventos da agenda pessoal ("primary") do usuário e os converte
     * para o modelo de domínio Agendamento - usado para importar/sugerir
     * eventos que o próprio usuário já criou manualmente no Google.
     */
    List<Agendamento> buscarEventosDaAgenda(String accessToken);

    /**
     * Cria uma agenda secundária dedicada (ex.: uma por Paciente), evitando que
     * agendamentos de pacientes diferentes se misturem na agenda "primary" do
     * cuidador quando ele gerencia mais de um paciente (RF06). Retorna o id da
     * agenda criada.
     */
    String criarCalendario(String accessToken, String nomeCalendario);

    /**
     * Cria um evento na agenda informada e retorna o id gerado (RF06, UC03).
     */
    String criarEvento(String accessToken, String calendarId, Agendamento agendamento);

    /**
     * Atualiza um evento já existente (ex.: reagendamento - RF06, UC04).
     */
    void atualizarEvento(String accessToken, String calendarId, String googleEventId, Agendamento agendamento);

    /**
     * Remove um evento da agenda (ex.: cancelamento - RF06, UC04).
     */
    void removerEvento(String accessToken, String calendarId, String googleEventId);
}
