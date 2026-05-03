package br.com.healthwallet.web.dto;


public record ProfissionalRequest(
        String nomeProfissional,
        String especialidade,
        String contato,
        String nomeClinica,
        String email,
        String numeroIdentificacaoProfissional,
        EnderecoRequest endereco
) {}
