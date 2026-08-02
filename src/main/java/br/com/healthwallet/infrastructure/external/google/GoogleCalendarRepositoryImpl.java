package br.com.healthwallet.infrastructure.external.google;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.repository.GoogleCalendarRepository;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.calendar.Calendar;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.google.api.client.util.DateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoogleCalendarRepositoryImpl implements GoogleCalendarRepository {

    private final GoogleEventMapper googleMapper;

    @Override
    public List<Agendamento> buscarEventosDaAgenda(String accessToken) {
        try {
            HttpRequestInitializer requestInitializer = request -> {
                request.getHeaders().setAuthorization("Bearer " + accessToken);
            };

            Calendar service = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName("Health Wallet")
                    .build();

            ZonedDateTime inicioDoDia = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
            DateTime timeMin = new DateTime(inicioDoDia.toInstant().toEpochMilli());

            Events events = service.events().list("primary")
                    .setTimeMin(timeMin)
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .setMaxResults(50)
                    .execute();

            List<com.google.api.services.calendar.model.Event> items = events.getItems();

            if (items == null || items.isEmpty()) {
                return new ArrayList<>();
            }

            return items.stream()
                    .map(googleMapper::toDomain)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar agenda do Google: " + e.getMessage());
        }
    }

    @Override
    public String criarCalendario(String accessToken, String nomeCalendario) {
        try {
            Calendar service = construirServico(accessToken);

            com.google.api.services.calendar.model.Calendar novaAgenda =
                    new com.google.api.services.calendar.model.Calendar().setSummary(nomeCalendario);

            com.google.api.services.calendar.model.Calendar criada = service.calendars().insert(novaAgenda).execute();
            return criada.getId();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar agenda no Google Calendar: " + e.getMessage(), e);
        }
    }

    @Override
    public String criarEvento(String accessToken, String calendarId, Agendamento agendamento) {
        try {
            Calendar service = construirServico(accessToken);
            com.google.api.services.calendar.model.Event evento = googleMapper.toGoogleEvent(agendamento);
            com.google.api.services.calendar.model.Event criado = service.events().insert(calendarId, evento).execute();
            return criado.getId();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar evento no Google Calendar: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizarEvento(String accessToken, String calendarId, String googleEventId, Agendamento agendamento) {
        try {
            Calendar service = construirServico(accessToken);
            com.google.api.services.calendar.model.Event evento = googleMapper.toGoogleEvent(agendamento);
            service.events().update(calendarId, googleEventId, evento).execute();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar evento no Google Calendar: " + e.getMessage(), e);
        }
    }

    @Override
    public void removerEvento(String accessToken, String calendarId, String googleEventId) {
        try {
            Calendar service = construirServico(accessToken);
            service.events().delete(calendarId, googleEventId).execute();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover evento do Google Calendar: " + e.getMessage(), e);
        }
    }

    private Calendar construirServico(String accessToken) throws Exception {
        HttpRequestInitializer requestInitializer = request ->
                request.getHeaders().setAuthorization("Bearer " + accessToken);

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Health Wallet")
                .build();
    }
}
