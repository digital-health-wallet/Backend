package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Paciente;

/**
 * @param alergia null quando o paciente não tem alergia cadastrada.
 */
public record PacienteComAlergia(Paciente paciente, Alergia alergia) {
}
