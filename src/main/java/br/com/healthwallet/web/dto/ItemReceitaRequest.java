package br.com.healthwallet.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ItemReceitaRequest(
        Long id,
        @NotNull @Valid MedicamentoRequest medicamento,
        String posologia,
        Boolean usoContinuo
) {
}
