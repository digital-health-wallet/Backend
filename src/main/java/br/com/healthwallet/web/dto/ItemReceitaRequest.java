package br.com.healthwallet.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** RF09 - cada medicamento da receita, com posologia e marcação de uso contínuo. */
public record ItemReceitaRequest(
        Long id,
        @NotNull @Valid MedicamentoRequest medicamento,
        String posologia,
        Boolean usoContinuo
) {
}
