package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.model.enums.TipoConsulta;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponse(
        Long id,
        Long idPaciente,
        String especialidade,
        String nomeClinica,
        String motivoConsulta,
        TipoConsulta tipoConsulta,
        LocalDate dataAgendamento,
        LocalTime horaAgendamento,
        LocalTime horaFim,
        StatusAgendamento status,
        Boolean favorito,
        Boolean arquivado
) {
    public static AgendamentoResponse from(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getIdPaciente(),
                agendamento.getEspecialidade(),
                agendamento.getNomeClinica(),
                agendamento.getMotivoConsulta(),
                agendamento.getTipoConsulta(),
                agendamento.getDataAgendamento(),
                agendamento.getHoraAgendamento(),
                agendamento.getHoraFim(),
                agendamento.getStatus(),
                agendamento.getFavorito(),
                agendamento.getArquivado()
        );
    }
}
