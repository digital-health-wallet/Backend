package br.com.healthwallet.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolve o id do Usuário autenticado a partir do JWT já validado pelo JwtAuthenticationFilter.
 */
public final class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static Long idOuFalhar() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        if (!(principal instanceof Long idUsuario)) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return idUsuario;
    }
}
