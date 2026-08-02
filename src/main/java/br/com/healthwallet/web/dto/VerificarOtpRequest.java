package br.com.healthwallet.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificarOtpRequest(
        @NotBlank @Email String email,
        @NotBlank String codigo
) {
}
