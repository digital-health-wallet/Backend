package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.model.enums.TipoConsulta;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponse(
        Long id,
        Long idPaciente,
        Long idProfissional,
        String especialidade,
        String nomeClinica,
        String motivoConsulta,
        TipoConsulta tipoConsulta,
        LocalDate dataAgendamento,
        LocalTime horaAgendamento,
        LocalTime horaFim,
        StatusAgendamento status,
        Boolean favorito,
        Boolean arquivado,
        String googleEventId,
        String avisoGoogle
) {
    public static AgendamentoResponse from(Agendamento agendamento) {
        return from(agendamento, null);
    }

    public static AgendamentoResponse from(Agendamento agendamento, String avisoGoogle) {
        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getIdPaciente(),
                agendamento.getIdProfissional(),
                agendamento.getEspecialidade(),
                agendamento.getNomeClinica(),
                agendamento.getMotivoConsulta(),
                agendamento.getTipoConsulta(),
                agendamento.getDataAgendamento(),
                agendamento.getHoraAgendamento(),
                agendamento.getHoraFim(),
                agendamento.getStatus(),
                agendamento.getFavorito(),
                agendamento.getArquivado(),
                agendamento.getGoogleEventId(),
                avisoGoogle
        );
    }
}
