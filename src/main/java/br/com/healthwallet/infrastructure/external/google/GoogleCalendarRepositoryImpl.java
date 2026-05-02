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

            // 1. Pega o início do dia de hoje para não perder consultas da manhã
            ZonedDateTime inicioDoDia = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
            DateTime timeMin = new DateTime(inicioDoDia.toInstant().toEpochMilli());

            //filtro personalizado para facilitar a listagem de eventos
            String queryMedica = "consulta médico exame hospital retorno dentista";

            Events events = service.events().list("primary")
                    //.setQ(queryMedica)
                    .setTimeMin(timeMin)           // Filtra de hoje em diante
                    .setSingleEvents(true)         // Separa os eventos que se repetem toda semana
                    .setOrderBy("startTime")       // Ordena do mais perto para o mais longe
                    .setMaxResults(50)
                    .execute();

            List<com.google.api.services.calendar.model.Event> items = events.getItems();

            System.out.println("========== DEBUG DO GOOGLE ==========");
            System.out.println("Eventos achados na API: " + (items != null ? items.size() : 0));
            if (items != null) {
                items.forEach(i -> System.out.println("Título: " + i.getSummary()));
            }
            System.out.println("=====================================");

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
}