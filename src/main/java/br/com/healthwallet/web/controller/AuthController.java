package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.AuthUseCase;
import br.com.healthwallet.infrastructure.security.AuthenticatedUser;
import br.com.healthwallet.web.dto.AuthResponse;
import br.com.healthwallet.web.dto.GoogleAuthUrlResponse;
import br.com.healthwallet.web.dto.SolicitarOtpRequest;
import br.com.healthwallet.web.dto.VerificarOtpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @Value("${app.frontend.google-callback-url}")
    private String frontendGoogleCallbackUrl;

    @PostMapping("/otp/solicitar")
    public ResponseEntity<Void> solicitarOtp(@Valid @RequestBody SolicitarOtpRequest request) {
        authUseCase.solicitarOtp(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/otp/verificar")
    public ResponseEntity<AuthResponse> verificarOtp(@Valid @RequestBody VerificarOtpRequest request) {
        return ResponseEntity.ok(authUseCase.verificarOtp(request.email(), request.codigo()));
    }

    /**
     * Chamado via XHR pelo Angular (não é navegação de página, então JSON é adequado aqui).
     * Se o usuário já estiver logado e quiser conectar o Calendar (RF06), o front deve mandar
     * o próprio JWT em "state"; ele volta intacto no callback, sem precisar de sessão no backend.
     */
    @GetMapping("/google/url")
    public ResponseEntity<GoogleAuthUrlResponse> urlConsentimentoGoogle(@RequestParam(required = false) String state) {
        return ResponseEntity.ok(new GoogleAuthUrlResponse(authUseCase.gerarUrlConsentimentoGoogle(state)));
    }

    /**
     * Retorno do consentimento do Google: é o próprio navegador que chega aqui (redirect do
     * Google), não uma chamada XHR do Angular — por isso a resposta precisa ser um redirect
     * HTTP de volta para o frontend com o JWT na querystring, e não um corpo JSON.
     */
    @GetMapping("/google/callback")
    public ResponseEntity<Void> callbackGoogle(@RequestParam String code,
                                                @RequestParam(required = false) String state) {
        Long idUsuarioLogado = extrairUsuarioDoState(state);
        AuthResponse resposta = authUseCase.processarCallbackGoogle(code, idUsuarioLogado);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendGoogleCallbackUrl)
                .queryParam("token", resposta.token())
                .queryParam("idUsuario", resposta.idUsuario())
                .queryParam("calendarConectado", resposta.calendarConectado())
                .build()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }

    @DeleteMapping("/conta")
    public ResponseEntity<Void> inativarConta() {
        authUseCase.inativarConta(AuthenticatedUser.idOuFalhar());
        return ResponseEntity.noContent().build();
    }

    private Long extrairUsuarioDoState(String state) {
        return authUseCase.idUsuarioDoJwtOuNulo(state);
    }
}
