package br.com.healthwallet.infrastructure.external.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC01 - Teste previsto na especificação: o código OTP é de uso único e não pode
 * ser reaproveitado após o primeiro login bem-sucedido.
 */
class OtpServiceTest {

    private static final String EMAIL = "paciente@exemplo.com";

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
    }

    @Test
    @DisplayName("Código válido autentica na primeira tentativa")
    void deveAceitarCodigoValido() {
        String codigo = otpService.gerarCodigo(EMAIL);

        assertThat(otpService.validarEInvalidar(EMAIL, codigo)).isTrue();
    }

    @Test
    @DisplayName("O mesmo código não pode ser reutilizado após o login")
    void naoDeveReutilizarCodigo() {
        String codigo = otpService.gerarCodigo(EMAIL);
        otpService.validarEInvalidar(EMAIL, codigo);

        assertThat(otpService.validarEInvalidar(EMAIL, codigo)).isFalse();
    }

    @Test
    @DisplayName("Reenviar o código invalida o anterior")
    void reenvioInvalidaCodigoAnterior() {
        String primeiro = otpService.gerarCodigo(EMAIL);
        String segundo = otpService.gerarCodigo(EMAIL);

        assertThat(otpService.validarEInvalidar(EMAIL, primeiro)).isFalse();
        assertThat(otpService.validarEInvalidar(EMAIL, segundo)).isTrue();
    }

    @Test
    @DisplayName("Código incorreto é recusado sem consumir o código vigente")
    void codigoIncorretoNaoConsomeOVigente() {
        String codigo = otpService.gerarCodigo(EMAIL);

        assertThat(otpService.validarEInvalidar(EMAIL, "000000")).isFalse();
        assertThat(otpService.validarEInvalidar(EMAIL, codigo)).isTrue();
    }

    @Test
    @DisplayName("Não há código para um e-mail que nunca solicitou")
    void semSolicitacaoNaoValida() {
        assertThat(otpService.validarEInvalidar("outro@exemplo.com", "123456")).isFalse();
    }
}
