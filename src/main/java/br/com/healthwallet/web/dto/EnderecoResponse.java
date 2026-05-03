package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Endereco;

public record EnderecoResponse(
        Long id,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String complemento
) {
    public static EnderecoResponse from(Endereco endereco) {
        if (endereco == null) return null;

        return new EnderecoResponse(
                endereco.getId(),
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getComplemento()
        );
    }
}
