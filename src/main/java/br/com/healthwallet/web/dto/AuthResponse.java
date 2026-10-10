package br.com.healthwallet.web.dto;

import br.com.healthwallet.application.usecase.ResultadoAutenticacao;

public record AuthResponse(
        String token,
        Long idUsuario,
        String email,
        boolean calendarConectado
) {
    public static AuthResponse from(ResultadoAutenticacao resultado) {
        if (resultado == null) return null;

        return new AuthResponse(resultado.token(), resultado.idUsuario(),
                resultado.email(), resultado.calendarConectado());
    }
}
