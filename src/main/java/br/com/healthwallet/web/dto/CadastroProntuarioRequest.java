package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.enums.TipoAlergia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CadastroProntuarioRequest(
        @NotBlank String cpf,
        @NotBlank String nome,
        @NotNull LocalDate dataNascimento,
        String tipoSanguineo,
        @NotNull Boolean fichaEmergencialAtiva,

        @NotNull Boolean possuiAlergia,
        TipoAlergia tipoAlergia,
        String descricaoAlergia,

        Boolean usaMedicamentoContinuo,
        List<MedicamentoContinuoRequest> medicamentosContinuos
) {
    public record MedicamentoContinuoRequest(String nome, String posologia) {
    }
}
