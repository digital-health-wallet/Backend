package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.GoogleCalendarRepository;
import br.com.healthwallet.domain.repository.ProfissionalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import br.com.healthwallet.domain.exception.ConflitoAgendamentoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * RF06/UC03 - A sincronização com o Google Calendar é opcional e sua falha nunca
 * pode impedir que a consulta seja salva localmente.
 */
@ExtendWith(MockitoExtension.class)
class AgendamentoUseCaseTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private ProfissionalRepository profissionalRepository;
    @Mock
    private GoogleCalendarRepository googleCalendarRepository;
    @Mock
    private GoogleTokenUseCase googleTokenUseCase;

    @InjectMocks
    private AgendamentoUseCase agendamentoUseCase;

    private Agendamento novoAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(10L);
        agendamento.setIdPaciente(1L);
        agendamento.setIdProfissional(2L);
        agendamento.setEspecialidade("Cardiologia");
        return agendamento;
    }

    @Test
    @DisplayName("Não aciona a API do Google quando o usuário não pediu sincronização")
    void naoDeveChamarGoogleSemSincronizacao() {
        Agendamento agendamento = novoAgendamento();
        when(agendamentoRepository.salvar(any())).thenReturn(agendamento);

        ResultadoAgendamento resultado = agendamentoUseCase.criar(agendamento, false, 1L);

        assertThat(resultado.avisoGoogle()).isNull();
        assertThat(resultado.agendamento()).isNotNull();
        assertThat(resultado.agendamento().getGoogleEventId()).isNull();
        verifyNoInteractions(googleCalendarRepository);
    }

    @Test
    @DisplayName("Salva a consulta e devolve aviso quando a sincronização com o Google falha")
    void deveSalvarLocalmenteMesmoComFalhaNoGoogle() {
        Agendamento agendamento = novoAgendamento();
        when(agendamentoRepository.salvar(any())).thenReturn(agendamento);
        when(googleTokenUseCase.obterAccessTokenValido(anyLong()))
                .thenThrow(new IllegalStateException("Usuário não conectou sua conta do Google Calendar."));

        ResultadoAgendamento resultado = agendamentoUseCase.criar(agendamento, true, 1L);

        assertThat(resultado.agendamento()).isNotNull();
        assertThat(resultado.avisoGoogle()).contains("não foi possível sincronizar");
        verify(agendamentoRepository).salvar(any());
    }

    @Test
    @DisplayName("Novo agendamento nasce com status AGENDADO e desarquivado")
    void deveDefinirEstadoInicial() {
        Agendamento agendamento = novoAgendamento();
        when(agendamentoRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        ResultadoAgendamento resultado = agendamentoUseCase.criar(agendamento, false, 1L);

        assertThat(resultado.agendamento().getStatus()).isEqualTo(StatusAgendamento.AGENDADO);
        assertThat(resultado.agendamento().getArquivado()).isFalse();
        assertThat(resultado.agendamento().getFavorito()).isFalse();
    }

    @Test
    @DisplayName("Arquivar e desarquivar alternam a flag do agendamento")
    void deveArquivarEDesarquivar() {
        Agendamento agendamento = novoAgendamento();
        agendamento.setArquivado(false);
        when(agendamentoRepository.buscarPorId(10L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.atualizar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        assertThat(agendamentoUseCase.arquivar(10L).getArquivado()).isTrue();
        assertThat(agendamentoUseCase.desarquivar(10L).getArquivado()).isFalse();
    }

    private Agendamento agendamentoEm(Long id, String hora, String horaFim) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setIdPaciente(1L);
        agendamento.setEspecialidade("Cardiologia");
        agendamento.setDataAgendamento(LocalDate.of(2026, 5, 20));
        agendamento.setHoraAgendamento(LocalTime.parse(hora));
        if (horaFim != null) {
            agendamento.setHoraFim(LocalTime.parse(horaFim));
        }
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        return agendamento;
    }

    @Test
    @DisplayName("Bloqueia o cadastro quando o paciente já tem consulta no mesmo intervalo")
    void deveBloquearChoqueDeAgendas() {
        Agendamento existente = agendamentoEm(10L, "09:00", "10:00");
        Agendamento novo = agendamentoEm(null, "09:30", "10:30");

        when(agendamentoRepository.buscarPorPacienteEData(1L, novo.getDataAgendamento()))
                .thenReturn(List.of(existente));

        assertThatThrownBy(() -> agendamentoUseCase.criar(novo, false, 1L))
                .isInstanceOf(ConflitoAgendamentoException.class)
                .hasMessageContaining("choque de agendas");

        verify(agendamentoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Permite o cadastro quando os intervalos apenas se encostam")
    void devePermitirHorariosSequenciais() {
        Agendamento existente = agendamentoEm(10L, "09:00", "10:00");
        Agendamento novo = agendamentoEm(null, "10:00", "11:00");

        when(agendamentoRepository.buscarPorPacienteEData(1L, novo.getDataAgendamento()))
                .thenReturn(List.of(existente));
        when(agendamentoRepository.salvar(any())).thenReturn(novo);

        assertThat(agendamentoUseCase.criar(novo, false, 1L).agendamento()).isNotNull();
    }

    @Test
    @DisplayName("Consulta cancelada não ocupa horário")
    void consultaCanceladaNaoBloqueia() {
        Agendamento cancelada = agendamentoEm(10L, "09:00", "10:00");
        cancelada.setStatus(StatusAgendamento.CANCELADO);
        Agendamento novo = agendamentoEm(null, "09:00", "10:00");

        when(agendamentoRepository.buscarPorPacienteEData(1L, novo.getDataAgendamento()))
                .thenReturn(List.of(cancelada));
        when(agendamentoRepository.salvar(any())).thenReturn(novo);

        assertThat(agendamentoUseCase.criar(novo, false, 1L).agendamento()).isNotNull();
    }

    @Test
    @DisplayName("Sem hora de término, considera uma hora de duração para detectar o conflito")
    void deveUsarDuracaoPadraoQuandoNaoHaHoraFim() {
        Agendamento existente = agendamentoEm(10L, "09:00", null);
        Agendamento novo = agendamentoEm(null, "09:45", null);

        when(agendamentoRepository.buscarPorPacienteEData(1L, novo.getDataAgendamento()))
                .thenReturn(List.of(existente));

        assertThatThrownBy(() -> agendamentoUseCase.criar(novo, false, 1L))
                .isInstanceOf(ConflitoAgendamentoException.class);
    }

    @Test
    @DisplayName("Cancelamento altera o status e preserva o registro, sem exclusão física")
    void cancelamentoPreservaORegistro() {
        Agendamento agendamento = novoAgendamento();
        agendamento.setStatus(StatusAgendamento.AGENDADO);

        when(agendamentoRepository.buscarPorId(10L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.atualizar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        ResultadoAgendamento resultado =
                agendamentoUseCase.atualizarStatus(10L, StatusAgendamento.CANCELADO, 1L);

        assertThat(resultado.agendamento().getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
        verify(agendamentoRepository).atualizar(any());
        verify(agendamentoRepository, never()).deletar(anyLong());
    }

    @Test
    @DisplayName("Consulta finalizada não pode ser cancelada nem reagendada")
    void consultaFinalizadaNaoMudaDeStatus() {
        Agendamento agendamento = novoAgendamento();
        agendamento.setStatus(StatusAgendamento.FINALIZADO);

        when(agendamentoRepository.buscarPorId(10L)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> agendamentoUseCase.atualizarStatus(10L, StatusAgendamento.CANCELADO, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalizada");

        verify(agendamentoRepository, never()).atualizar(any());
    }
}
