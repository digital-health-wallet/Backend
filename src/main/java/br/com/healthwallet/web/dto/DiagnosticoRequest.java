package br.com.healthwallet.web.dto;

import jakarta.validation.constraints.NotBlank;

public record DiagnosticoRequest(
        Long idAgendamento,
        Long idPaciente,
        @NotBlank String nome,
        String cid,
        String descricao,
        Boolean doencaCronica
) {
}
