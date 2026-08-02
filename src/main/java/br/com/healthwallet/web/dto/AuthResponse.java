package br.com.healthwallet.web.dto;

public record AuthResponse(
        String token,
        Long idUsuario,
        String email,
        boolean calendarConectado
) {
}
