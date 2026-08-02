package br.com.healthwallet.infrastructure.external.email;

public interface EmailService {
    void enviarCodigoOtp(String destinatario, String codigo);
}
