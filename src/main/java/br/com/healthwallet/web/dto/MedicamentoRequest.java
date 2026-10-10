package br.com.healthwallet.web.dto;

import jakarta.validation.constraints.NotBlank;

public record MedicamentoRequest(
        Long id,
        @NotBlank String nomeMedicamento,
        String laboratorio,
        String feedback
) {
}
