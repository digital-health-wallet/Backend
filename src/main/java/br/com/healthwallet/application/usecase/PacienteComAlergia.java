package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Paciente;

import java.util.List;

/**
 * @param alergias RF10 - o paciente pode ter várias alergias registradas; lista vazia
 *                 quando nenhuma foi informada.
 */
public record PacienteComAlergia(
        Paciente paciente,
        List<Alergia> alergias,
        List<Diagnostico> diagnosticosCronicos,
        List<ItemReceita> medicamentosUsoContinuo
) {
}
