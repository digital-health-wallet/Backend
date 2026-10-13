package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.model.Usuario;
import br.com.healthwallet.domain.repository.GoogleCalendarRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.UsuarioRepository;
import br.com.healthwallet.infrastructure.external.google.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class GoogleTokenUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final GoogleOAuthService googleOAuthService;
    private final GoogleCalendarRepository googleCalendarRepository;

    public String obterAccessTokenValido(Long idUsuario) {
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + idUsuario));

        if (!usuario.possuiCalendarConectado()) {
            throw new IllegalStateException("Usuário não conectou sua conta do Google Calendar.");
        }

        boolean expirado = usuario.getGoogleTokenExpiration() == null
                || LocalDateTime.now().isAfter(usuario.getGoogleTokenExpiration().minusMinutes(1));

        if (!expirado) {
            return usuario.getGoogleAccessToken();
        }

        GoogleOAuthService.GoogleTokenResult resultado = googleOAuthService.renovarAccessToken(usuario.getGoogleRefreshToken());

        usuario.setGoogleAccessToken(resultado.getAccessToken());
        usuario.setGoogleTokenExpiration(resultado.getExpiraEm());
        usuarioRepository.salvar(usuario);

        return resultado.getAccessToken();
    }


    @Transactional
    public String obterCalendarioDoPaciente(Long idPaciente, Long idUsuario) {
        Paciente paciente = pacienteRepository.buscarPorId(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado: " + idPaciente));

        if (paciente.getGoogleCalendarId() != null) {
            return paciente.getGoogleCalendarId();
        }

        String accessToken = obterAccessTokenValido(idUsuario);
        String calendarId = googleCalendarRepository.criarCalendario(accessToken, "Health Wallet - " + paciente.getNome());

        paciente.setGoogleCalendarId(calendarId);
        pacienteRepository.salvar(paciente);

        return calendarId;
    }
}
