package br.com.healthwallet.web.dto;

public record ContatoRequest(
        String nome,
        String parentesco,
        String telefone,
        String email
) {}
