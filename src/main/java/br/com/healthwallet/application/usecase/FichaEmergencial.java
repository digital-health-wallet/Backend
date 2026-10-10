package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Paciente;

import java.util.List;

/**
 * UC06 - Os dados vitais que a ficha pública pode expor (RF13), em termos de
 * domínio. A redução ao formato devolvido pela API acontece no controlador.
 */
public record FichaEmergencial(
        Paciente paciente,
        List<Alergia> alergias,
        List<Diagnostico> diagnosticosCronicos,
        List<ItemReceita> medicamentosUsoContinuo
) {
}
