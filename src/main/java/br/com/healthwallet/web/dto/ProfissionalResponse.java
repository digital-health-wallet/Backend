package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Profissional;

public record ProfissionalResponse(
        Long id,
        String nomeProfissional,
        String numeroIdentificacaoProfissional,
        String contato,
        String nomeClinica,
        String email,
        String especialidade,
        EnderecoResponse endereco // Traz o endereço aninhado, se existir
) {
    public static ProfissionalResponse from(Profissional profissional) {
        if (profissional == null) return null;

        return new ProfissionalResponse(
                profissional.getId(),
                profissional.getNomeProfissional(),
                profissional.getNumeroIdentificacaoProfissional(),
                profissional.getContato(),
                profissional.getNomeClinica(),
                profissional.getEmail(),
                profissional.getEspecialidade(),
                // Se o profissional tiver endereço, converte usando o padrão do EnderecoResponse
                profissional.getEndereco() != null ? EnderecoResponse.from(profissional.getEndereco()) : null
        );
    }
}