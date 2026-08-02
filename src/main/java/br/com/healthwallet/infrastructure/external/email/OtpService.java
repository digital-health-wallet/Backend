package br.com.healthwallet.infrastructure.external.email;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Geração e validação do código OTP (UC01), mantido em memória por ser
 * de curtíssima duração (5 minutos) e de uso único.
 */
@Component
public class OtpService {

    private static final Duration VALIDADE = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<String, OtpEntry> codigosPorEmail = new ConcurrentHashMap<>();

    public String gerarCodigo(String email) {
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        codigosPorEmail.put(email.toLowerCase(), new OtpEntry(codigo, Instant.now().plus(VALIDADE)));
        return codigo;
    }

    public boolean validarEInvalidar(String email, String codigoInformado) {
        OtpEntry entry = codigosPorEmail.get(email.toLowerCase());

        if (entry == null || Instant.now().isAfter(entry.expiraEm())) {
            codigosPorEmail.remove(email.toLowerCase());
            return false;
        }

        boolean valido = entry.codigo().equals(codigoInformado);
        if (valido) {
            codigosPorEmail.remove(email.toLowerCase());
        }
        return valido;
    }

    private record OtpEntry(String codigo, Instant expiraEm) {
    }
}
