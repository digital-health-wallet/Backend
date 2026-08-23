package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import br.com.healthwallet.web.dto.EmergenciaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * UC06 - Ficha de emergência é rota pública: os testes garantem que dados clínicos
 * só são expostos quando o paciente está ativo E autorizou a ficha.
 */
@ExtendWith(MockitoExtension.class)
class EmergenciaUseCaseTest {

    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private AlergiaRepository alergiaRepository;
    @Mock
    private DiagnosticoRepository diagnosticoRepository;
    @Mock
    private ReceitaRepository receitaRepository;

    @InjectMocks
    private EmergenciaUseCase emergenciaUseCase;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("Maria");
        paciente.setTipoSanguineo("O+");
        paciente.setAtivo(true);
        paciente.setFichaEmergencialAtiva(true);
    }

    @Test
    @DisplayName("Retorna dados vitais quando o paciente está ativo e liberou a ficha")
    void deveRetornarFichaQuandoLiberada() {
        Diagnostico cronico = new Diagnostico();
        cronico.setNome("Hipertensão");
        cronico.setCid("I10");

        when(pacienteRepository.buscarPorCodigoEmergencia("COD-123")).thenReturn(Optional.of(paciente));
        when(alergiaRepository.buscarPorPaciente(1L)).thenReturn(List.of());
        when(diagnosticoRepository.buscarCronicosPorPaciente(1L)).thenReturn(List.of(cronico));
        when(receitaRepository.buscarItensUsoContinuoPorPaciente(1L)).thenReturn(List.of());

        EmergenciaResponse ficha = emergenciaUseCase.buscarFichaPublica("COD-123");

        assertThat(ficha.nome()).isEqualTo("Maria");
        assertThat(ficha.tipoSanguineo()).isEqualTo("O+");
        assertThat(ficha.diagnosticosCronicos()).hasSize(1);
        assertThat(ficha.diagnosticosCronicos().get(0).nome()).isEqualTo("Hipertensão");
    }

    @Test
    @DisplayName("Nega acesso quando o paciente desativou a ficha de emergência")
    void deveNegarQuandoFichaDesativada() {
        paciente.setFichaEmergencialAtiva(false);
        when(pacienteRepository.buscarPorCodigoEmergencia("COD-123")).thenReturn(Optional.of(paciente));

        assertThatThrownBy(() -> emergenciaUseCase.buscarFichaPublica("COD-123"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("Nega acesso quando o paciente está inativo")
    void deveNegarQuandoPacienteInativo() {
        paciente.setAtivo(false);
        when(pacienteRepository.buscarPorCodigoEmergencia("COD-123")).thenReturn(Optional.of(paciente));

        assertThatThrownBy(() -> emergenciaUseCase.buscarFichaPublica("COD-123"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("Nega acesso quando o código de emergência não existe")
    void deveNegarQuandoCodigoInexistente() {
        when(pacienteRepository.buscarPorCodigoEmergencia("INVALIDO")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emergenciaUseCase.buscarFichaPublica("INVALIDO"))
                .isInstanceOf(SecurityException.class);
    }
}
