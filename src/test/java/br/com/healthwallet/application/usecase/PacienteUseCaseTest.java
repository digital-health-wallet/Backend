package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.model.enums.TipoAlergia;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import br.com.healthwallet.web.dto.AlergiaRequest;
import br.com.healthwallet.web.dto.PacienteUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF10 - O paciente pode ter várias alergias registradas, e a remoção de uma
 * delas é lógica: o registro é desativado, nunca apagado do banco.
 */
@ExtendWith(MockitoExtension.class)
class PacienteUseCaseTest {

    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private AlergiaRepository alergiaRepository;
    @Mock
    private DiagnosticoRepository diagnosticoRepository;
    @Mock
    private ReceitaRepository receitaRepository;

    @InjectMocks
    private PacienteUseCase pacienteUseCase;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setIdUsuario(9L);
        paciente.setNome("Maria");
    }

    private Alergia alergia(Long id, String descricao) {
        Alergia alergia = new Alergia();
        alergia.setId(id);
        alergia.setIdPaciente(1L);
        alergia.setTipo(TipoAlergia.M);
        alergia.setDescricao(descricao);
        alergia.setAtivo(true);
        return alergia;
    }

    private PacienteUpdateRequest requisicao(List<AlergiaRequest> alergias) {
        return new PacienteUpdateRequest("Maria", "333.333.333-90", LocalDate.of(1990, 1, 1),
                "O+", true, alergias != null && !alergias.isEmpty(), alergias);
    }

    private void prepararPaciente() {
        when(pacienteRepository.buscarPorId(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.salvar(any())).thenReturn(paciente);
    }

    @Test
    @DisplayName("Grava todas as alergias informadas para o paciente")
    void devePersistirVariasAlergias() {
        prepararPaciente();
        when(alergiaRepository.buscarPorPaciente(1L)).thenReturn(List.of());
        when(alergiaRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        PacienteComAlergia resultado = pacienteUseCase.atualizar(1L, 9L, requisicao(List.of(
                new AlergiaRequest(null, TipoAlergia.M, "Dipirona"),
                new AlergiaRequest(null, TipoAlergia.A, "Amendoim"))));

        assertThat(resultado.alergias()).hasSize(2)
                .extracting(Alergia::getDescricao)
                .containsExactly("Dipirona", "Amendoim");
    }

    @Test
    @DisplayName("Alergia removida da tela é desativada, nunca apagada do banco")
    void remocaoEhLogica() {
        prepararPaciente();
        when(alergiaRepository.buscarPorPaciente(1L))
                .thenReturn(List.of(alergia(5L, "Dipirona"), alergia(6L, "Amendoim")));
        when(alergiaRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        // A tela devolve apenas a primeira: a segunda foi removida pelo usuário.
        pacienteUseCase.atualizar(1L, 9L, requisicao(List.of(
                new AlergiaRequest(5L, TipoAlergia.M, "Dipirona"))));

        verify(alergiaRepository).desativar(6L);
        verify(alergiaRepository, never()).desativar(5L);
    }

    @Test
    @DisplayName("Marcar que não possui alergia desativa todas as existentes")
    void semAlergiaDesativaTodas() {
        prepararPaciente();
        when(alergiaRepository.buscarPorPaciente(1L))
                .thenReturn(List.of(alergia(5L, "Dipirona"), alergia(6L, "Amendoim")));

        PacienteComAlergia resultado = pacienteUseCase.atualizar(1L, 9L, requisicao(List.of()));

        assertThat(resultado.alergias()).isEmpty();
        verify(alergiaRepository).desativar(5L);
        verify(alergiaRepository).desativar(6L);
        verify(alergiaRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Alergia sem descrição é ignorada, não grava registro vazio")
    void ignoraAlergiaSemDescricao() {
        prepararPaciente();
        when(alergiaRepository.buscarPorPaciente(1L)).thenReturn(List.of());

        PacienteComAlergia resultado = pacienteUseCase.atualizar(1L, 9L, requisicao(List.of(
                new AlergiaRequest(null, TipoAlergia.M, "   "))));

        assertThat(resultado.alergias()).isEmpty();
        verify(alergiaRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Perfil de outro usuário não pode ser alterado")
    void naoAlteraPacienteDeOutroUsuario() {
        when(pacienteRepository.buscarPorId(1L)).thenReturn(Optional.of(paciente));

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> pacienteUseCase.atualizar(1L, 99L, requisicao(List.of())))
                .isInstanceOf(SecurityException.class);

        verify(alergiaRepository, never()).desativar(anyLong());
    }
}
