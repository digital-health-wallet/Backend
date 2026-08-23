package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Paciente;

import java.util.List;

/**
 * @param alergia null quando o paciente não tem alergia cadastrada.
 */
public record PacienteComAlergia(
        Paciente paciente,
        Alergia alergia,
        List<Diagnostico> diagnosticosCronicos,
        List<ItemReceita> medicamentosUsoContinuo
) {
}
