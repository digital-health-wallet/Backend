package br.com.healthwallet.infrastructure.external.google;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Encapsula o fluxo de OAuth2 do Google (RF06, UC01): geração da URL de
 * consentimento, troca do código de autorização por tokens e renovação
 * de access_token via refresh_token.
 */
@Component
public class GoogleOAuthService {

    private static final Collection<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile",
            CalendarScopes.CALENDAR
    );

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public GoogleOAuthService(@Value("${google.oauth.client-id}") String clientId,
                               @Value("${google.oauth.client-secret}") String clientSecret,
                               @Value("${google.oauth.redirect-uri}") String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public boolean configurado() {
        return clientId != null && !clientId.isBlank() && clientSecret != null && !clientSecret.isBlank();
    }

    /**
     * @param state repassado pelo Google de volta ao callback (RFC 6749 §4.1.1). Usado aqui para
     *              carregar o JWT do usuário já logado, permitindo vincular o Calendar a uma conta
     *              existente sem depender de sessão/cookie no backend (fluxo stateless SPA).
     */
    public String gerarUrlConsentimento(String state) {
        exigirConfiguracao();
        GoogleAuthorizationCodeRequestUrl url = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUri, SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force");

        if (state != null && !state.isBlank()) {
            url.setState(state);
        }

        return url.build();
    }

    public GoogleTokenResult trocarCodigoPorToken(String code) {
        exigirConfiguracao();
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientId,
                    clientSecret,
                    code,
                    redirectUri)
                    .execute();

            String email = extrairEmail(tokenResponse.getIdToken());

            return new GoogleTokenResult(
                    email,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds() != null ? tokenResponse.getExpiresInSeconds() : 3600)
            );
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Erro ao autenticar com o Google: " + e.getMessage(), e);
        }
    }

    public GoogleTokenResult renovarAccessToken(String refreshToken) {
        exigirConfiguracao();
        try {
            GoogleTokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    refreshToken,
                    clientId,
                    clientSecret)
                    .execute();

            return new GoogleTokenResult(
                    null,
                    tokenResponse.getAccessToken(),
                    refreshToken,
                    LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds() != null ? tokenResponse.getExpiresInSeconds() : 3600)
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao renovar o token do Google: " + e.getMessage(), e);
        }
    }

    private String extrairEmail(String idToken) throws IOException, GeneralSecurityException {
        if (idToken == null) return null;

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(clientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken == null) {
            throw new RuntimeException("Token de identidade do Google inválido.");
        }
        return googleIdToken.getPayload().getEmail();
    }

    private void exigirConfiguracao() {
        if (!configurado()) {
            throw new IllegalStateException(
                    "Integração com Google não configurada. Defina GOOGLE_CLIENT_ID e GOOGLE_CLIENT_SECRET.");
        }
    }

    @Getter
    public static class GoogleTokenResult {
        private final String email;
        private final String accessToken;
        private final String refreshToken;
        private final LocalDateTime expiraEm;

        public GoogleTokenResult(String email, String accessToken, String refreshToken, LocalDateTime expiraEm) {
            this.email = email;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiraEm = expiraEm;
        }
    }
}
