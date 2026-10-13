package br.com.healthwallet.application.usecase;


public record ResultadoAutenticacao(
        String token,
        Long idUsuario,
        String email,
        boolean calendarConectado
) {
}
