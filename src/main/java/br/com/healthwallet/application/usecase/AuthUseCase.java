package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Usuario;
import br.com.healthwallet.domain.repository.UsuarioRepository;
import br.com.healthwallet.infrastructure.external.email.EmailService;
import br.com.healthwallet.infrastructure.external.email.OtpService;
import br.com.healthwallet.infrastructure.external.google.GoogleOAuthService;
import br.com.healthwallet.infrastructure.security.JwtService;
import br.com.healthwallet.web.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * UC01 - Autenticação e Gestão de Conta: login por e-mail (OTP) e login/consentimento
 * via Google (também usado para conectar o Google Calendar a uma conta já existente - RF06).
 */
@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final UsuarioRepository usuarioRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final GoogleOAuthService googleOAuthService;

    @Transactional
    public void solicitarOtp(String email) {
        Usuario usuario = buscarOuCriarUsuario(email);

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new IllegalStateException("Esta conta está inativa.");
        }

        String codigo = otpService.gerarCodigo(email);
        emailService.enviarCodigoOtp(email, codigo);
    }

    @Transactional
    public AuthResponse verificarOtp(String email, String codigo) {
        if (!otpService.validarEInvalidar(email, codigo)) {
            throw new IllegalArgumentException("Código inválido ou expirado.");
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado."));

        return gerarResposta(usuario);
    }

    /**
     * @param jwtUsuarioLogado quando informado (usuário já autenticado querendo conectar o
     *                         Calendar), é repassado como "state" para o Google e devolvido
     *                         intacto no callback (fluxo stateless, sem sessão no backend).
     */
    public String gerarUrlConsentimentoGoogle(String jwtUsuarioLogado) {
        return googleOAuthService.gerarUrlConsentimento(jwtUsuarioLogado);
    }

    /**
     * Processa o retorno do consentimento do Google. Se idUsuarioLogado for informado,
     * os tokens são vinculados à conta já autenticada (conectar calendário); caso
     * contrário, autentica (ou cria) o Usuário pelo e-mail retornado pelo Google.
     */
    @Transactional
    public AuthResponse processarCallbackGoogle(String code, Long idUsuarioLogado) {
        GoogleOAuthService.GoogleTokenResult tokenResult = googleOAuthService.trocarCodigoPorToken(code);

        Usuario usuario = idUsuarioLogado != null
                ? usuarioRepository.buscarPorId(idUsuarioLogado)
                        .orElseThrow(() -> new IllegalStateException("Usuário não encontrado."))
                : buscarOuCriarUsuario(tokenResult.getEmail());

        usuario.setGoogleAccessToken(tokenResult.getAccessToken());
        if (tokenResult.getRefreshToken() != null) {
            usuario.setGoogleRefreshToken(tokenResult.getRefreshToken());
        }
        usuario.setGoogleTokenExpiration(tokenResult.getExpiraEm());

        Usuario salvo = usuarioRepository.salvar(usuario);
        return gerarResposta(salvo);
    }

    @Transactional
    public void inativarConta(Long idUsuario) {
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado."));

        usuario.setAtivo(false);
        usuario.setDataDesativacao(LocalDateTime.now());
        usuarioRepository.salvar(usuario);
    }

    /**
     * Usado para decodificar o "state" do callback do Google: se for um JWT válido
     * (usuário já logado pedindo para conectar o Calendar), devolve o id; caso
     * contrário (login novo via Google), devolve null.
     */
    public Long idUsuarioDoJwtOuNulo(String possivelJwt) {
        if (possivelJwt == null || possivelJwt.isBlank() || !jwtService.tokenValido(possivelJwt)) {
            return null;
        }
        return jwtService.extrairIdUsuario(possivelJwt);
    }

    private Usuario buscarOuCriarUsuario(String email) {
        return usuarioRepository.buscarPorEmail(email)
                .orElseGet(() -> {
                    Usuario novo = new Usuario();
                    novo.setEmail(email);
                    novo.setAtivo(true);
                    return usuarioRepository.salvar(novo);
                });
    }

    private AuthResponse gerarResposta(Usuario usuario) {
        String token = jwtService.gerarToken(usuario.getId(), usuario.getEmail());
        return new AuthResponse(token, usuario.getId(), usuario.getEmail(), usuario.possuiCalendarConectado());
    }
}
