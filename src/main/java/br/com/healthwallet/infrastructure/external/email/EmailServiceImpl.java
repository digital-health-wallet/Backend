package br.com.healthwallet.infrastructure.external.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Enquanto app.mail.enabled=false (padrão em desenvolvimento, sem credenciais SMTP
 * configuradas), o código OTP é apenas logado no console. Assim que houver SMTP
 * configurado (spring.mail.*), basta setar app.mail.enabled=true para envio real.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled}")
    private boolean mailHabilitado;

    @Value("${app.mail.from}")
    private String remetente;

    @Override
    public void enviarCodigoOtp(String destinatario, String codigo) {
        if (!mailHabilitado) {
            log.info("[MODO DEV - OTP] Código de acesso para {}: {}", destinatario, codigo);
            return;
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Digital Health Wallet - Código de acesso");
        mensagem.setText("Seu código de acesso é: " + codigo + "\nEle expira em 5 minutos.");

        mailSender.send(mensagem);
    }
}
