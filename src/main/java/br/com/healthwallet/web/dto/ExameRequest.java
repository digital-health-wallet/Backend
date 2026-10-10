package br.com.healthwallet.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record ExameRequest(
        Long idAgendamento,
        Long idPaciente,
        @NotBlank String nomeExame,
        LocalDate dataHoraExame,
        String observacoes,
        List<UploadRequest> uploads
) {
}
