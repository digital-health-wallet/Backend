package br.com.healthwallet.web.dto;

/**
 * Corpo devolvido quando uma regra de negócio impede a operação, para que o
 * frontend possa exibir a mensagem ao usuário em vez de um erro genérico.
 */
public record ErroResponse(String mensagem) {
}
