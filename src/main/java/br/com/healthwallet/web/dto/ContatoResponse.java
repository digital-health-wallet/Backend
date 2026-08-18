package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Contato;

public record ContatoResponse(
        Long id,
        String nome,
        String parentesco,
        String telefone,
        String email
) {
    public static ContatoResponse from(Contato contato) {
        if (contato == null) return null;

        return new ContatoResponse(
                contato.getId(),
                contato.getNome(),
                contato.getParentesco(),
                contato.getTelefone(),
                contato.getEmail()
        );
    }
}
