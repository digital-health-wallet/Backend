package br.com.healthwallet.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PacienteUpdateRequest(
        @NotBlank String nome,
        String cpf,
        @NotNull LocalDate dataNascimento,
        String tipoSanguineo,
        @NotNull Boolean fichaEmergencialAtiva,

        @NotNull Boolean possuiAlergia,
        List<AlergiaRequest> alergias
) {
}
