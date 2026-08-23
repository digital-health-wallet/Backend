package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Todo documento clínico precisa ficar vinculado a um paciente: sem esse vínculo,
 * um documento avulso apareceria na ficha de emergência de todos os pacientes da conta.
 */
@ExtendWith(MockitoExtension.class)
class DiagnosticoUseCaseTest {

    @Mock
    private DiagnosticoRepository diagnosticoRepository;
    @Mock
    private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private DiagnosticoUseCase diagnosticoUseCase;

    @Test
    @DisplayName("Deriva o paciente a partir do agendamento quando o vínculo não é informado")
    void deveDerivarPacienteDoAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(5L);
        agendamento.setIdPaciente(42L);

        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setNome("Hipertensão");
        diagnostico.setIdAgendamento(5L);
        diagnostico.setIdPaciente(null);

        when(agendamentoRepository.buscarPorId(5L)).thenReturn(Optional.of(agendamento));
        when(diagnosticoRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        diagnosticoUseCase.salvar(diagnostico);

        ArgumentCaptor<Diagnostico> capturado = ArgumentCaptor.forClass(Diagnostico.class);
        verify(diagnosticoRepository).salvar(capturado.capture());
        assertThat(capturado.getValue().getIdPaciente()).isEqualTo(42L);
    }

    @Test
    @DisplayName("Preserva o paciente informado em diagnóstico avulso, sem consultar agendamento")
    void devePreservarPacienteDeDiagnosticoAvulso() {
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setNome("Diabetes");
        diagnostico.setIdAgendamento(null);
        diagnostico.setIdPaciente(7L);

        when(diagnosticoRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        diagnosticoUseCase.salvar(diagnostico);

        ArgumentCaptor<Diagnostico> capturado = ArgumentCaptor.forClass(Diagnostico.class);
        verify(diagnosticoRepository).salvar(capturado.capture());
        assertThat(capturado.getValue().getIdPaciente()).isEqualTo(7L);
        verifyNoInteractions(agendamentoRepository);
    }

    @Test
    @DisplayName("Exclusão é lógica: o registro é desativado, nunca apagado do banco")
    void deveDesativarEmVezDeExcluir() {
        diagnosticoUseCase.desativar(3L);

        verify(diagnosticoRepository).desativar(3L);
    }
}
