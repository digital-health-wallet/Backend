package br.com.healthwallet.infrastructure.external.google;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.model.enums.TipoConsulta;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class GoogleEventMapper {

    /**
     * Converte um Agendamento local em um Evento do Google Calendar (RF06, UC03/UC04).
     */
    public Event toGoogleEvent(Agendamento agendamento) {
        Event event = new Event();
        event.setSummary(agendamento.getEspecialidade());
        event.setLocation(agendamento.getNomeClinica());
        event.setDescription(agendamento.getMotivoConsulta());

        ZoneId zoneId = ZoneId.systemDefault();
        LocalTime horaFim = agendamento.getHoraFim() != null
                ? agendamento.getHoraFim()
                : agendamento.getHoraAgendamento().plusHours(1);

        ZonedDateTime inicio = ZonedDateTime.of(agendamento.getDataAgendamento(), agendamento.getHoraAgendamento(), zoneId);
        ZonedDateTime fim = ZonedDateTime.of(agendamento.getDataAgendamento(), horaFim, zoneId);

        event.setStart(new EventDateTime().setDateTime(new DateTime(inicio.toInstant().toEpochMilli())));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(fim.toInstant().toEpochMilli())));

        return event;
    }
    public Agendamento toDomain(Event googleEvent) {
        Agendamento agendamento = new Agendamento();

        String titulo = googleEvent.getSummary() != null ? googleEvent.getSummary() : "Consulta Importada";
        agendamento.setEspecialidade(titulo);

        agendamento.setNomeClinica(googleEvent.getLocation() != null ? googleEvent.getLocation() : "Local não informado");

        agendamento.setMotivoConsulta(googleEvent.getDescription());

        if (googleEvent.getStart() != null) {
            if (googleEvent.getStart().getDateTime() != null) {
                var start = googleEvent.getStart().getDateTime();
                var zonedDateTime = java.time.ZonedDateTime.parse(start.toStringRfc3339());
                agendamento.setDataAgendamento(zonedDateTime.toLocalDate());
                agendamento.setHoraAgendamento(zonedDateTime.toLocalTime());
            } else if (googleEvent.getStart().getDate() != null) {
                var dataDiaInteiro = java.time.LocalDate.parse(googleEvent.getStart().getDate().toStringRfc3339());
                agendamento.setDataAgendamento(dataDiaInteiro);
                agendamento.setHoraAgendamento(LocalTime.of(8, 0));
            }
        } else {
            agendamento.setDataAgendamento(LocalDate.now());
            agendamento.setHoraAgendamento(LocalTime.now().withNano(0));
        }

        // Com base no título do evento, setar o tipo da consulta, se não set a o default
        String tituloLowerCase = titulo.toLowerCase();
        if (tituloLowerCase.contains("exame") || tituloLowerCase.contains("sangue") || tituloLowerCase.contains("ultrassom") || tituloLowerCase.contains("raio-x") || tituloLowerCase.contains("ressonância")) {
            agendamento.setTipoConsulta(TipoConsulta.EXAME);
        } else if (tituloLowerCase.contains("retorno")) {
            agendamento.setTipoConsulta(TipoConsulta.RETORNO);
        } else if (tituloLowerCase.contains("emergência") || tituloLowerCase.contains("pronto socorro")) {
            agendamento.setTipoConsulta(TipoConsulta.EMERGENCIA);
        } else {
            agendamento.setTipoConsulta(TipoConsulta.CONSULTA);
        }

        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setFavorito(false);
        agendamento.setArquivado(false);
        agendamento.setGoogleEventId(googleEvent.getId());

        return agendamento;
    }
}