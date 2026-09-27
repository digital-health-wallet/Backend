package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.enums.TipoAlergia;

/**
 * RF10 - uma alergia do histórico do paciente. O id vem preenchido quando se
 * trata de um registro já existente sendo editado.
 */
public record AlergiaRequest(Long id, TipoAlergia tipo, String descricao) {
}
