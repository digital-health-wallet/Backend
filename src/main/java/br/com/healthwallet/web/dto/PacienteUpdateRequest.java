package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.enums.TipoAlergia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PacienteUpdateRequest(
        @NotBlank String nome,
        String cpf,
        @NotNull LocalDate dataNascimento,
        String tipoSanguineo,
        @NotNull Boolean fichaEmergencialAtiva,

        @NotNull Boolean possuiAlergia,
        TipoAlergia tipoAlergia,
        String descricaoAlergia
) {
}
