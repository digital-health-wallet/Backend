package br.com.healthwallet.application.usecase;

/**
 * Resultado de uma autenticação bem-sucedida (UC01), em termos da própria
 * aplicação. A conversão para o formato devolvido pela API acontece no
 * controlador.
 */
public record ResultadoAutenticacao(
        String token,
        Long idUsuario,
        String email,
        boolean calendarConectado
) {
}
