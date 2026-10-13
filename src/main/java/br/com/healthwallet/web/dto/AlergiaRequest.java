package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.enums.TipoAlergia;


public record AlergiaRequest(Long id, TipoAlergia tipo, String descricao) {
}
