package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.enums.TipoConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequest(
        @NotNull Long idPaciente,
        Long idProfissional,
        ProfissionalRequest profissional,
        @NotBlank String especialidade,
        String nomeClinica,
        String motivoConsulta,
        TipoConsulta tipoConsulta,
        @NotNull LocalDate dataAgendamento,
        @NotNull LocalTime horaAgendamento,
        LocalTime horaFim,
        Boolean sincronizarGoogle
) {
}
