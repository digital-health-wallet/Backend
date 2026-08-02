package br.com.healthwallet.web.dto;

import br.com.healthwallet.application.usecase.PacienteComAlergia;
import br.com.healthwallet.domain.model.enums.TipoAlergia;

import java.time.LocalDate;

public record PacienteResponse(
        Long id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String tipoSanguineo,
        Boolean fichaEmergencialAtiva,
        Boolean ativo,
        String codigoEmergencia,
        Boolean possuiAlergia,
        TipoAlergia tipoAlergia,
        String descricaoAlergia
) {
    public static PacienteResponse from(PacienteComAlergia dados) {
        boolean possuiAlergia = dados.alergia() != null;
        return new PacienteResponse(
                dados.paciente().getId(),
                dados.paciente().getNome(),
                dados.paciente().getCpf(),
                dados.paciente().getDataNascimento(),
                dados.paciente().getTipoSanguineo(),
                dados.paciente().getFichaEmergencialAtiva(),
                dados.paciente().getAtivo(),
                dados.paciente().getCodigoEmergencia(),
                possuiAlergia,
                possuiAlergia ? dados.alergia().getTipo() : null,
                possuiAlergia ? dados.alergia().getDescricao() : null
        );
    }
}
