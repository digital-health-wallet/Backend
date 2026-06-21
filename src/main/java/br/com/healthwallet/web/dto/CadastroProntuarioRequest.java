package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.enums.TipoAlergia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CadastroProntuarioRequest(
        @NotNull Long idUsuario,
        @NotBlank String cpf,
        @NotBlank String nome,
        @NotNull LocalDate dataNascimento,
        String tipoSanguineo,
        @NotNull Boolean fichaEmergencialAtiva,

        @NotNull Boolean possuiAlergia,
        TipoAlergia tipoAlergia,
        String descricaoAlergia
) {
}
