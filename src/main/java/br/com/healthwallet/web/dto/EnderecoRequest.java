package br.com.healthwallet.web.dto;

public record EnderecoRequest(
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String complemento
) {}