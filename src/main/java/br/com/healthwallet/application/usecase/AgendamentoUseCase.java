package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.exception.ConflitoAgendamentoException;
import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.Profissional;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.GoogleCalendarRepository;
import br.com.healthwallet.domain.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final GoogleCalendarRepository googleCalendarRepository;
    private final GoogleTokenUseCase googleTokenUseCase;
    private final AcessoPaciente acessoPaciente;

    public ResultadoAgendamento criar(Agendamento agendamento, boolean sincronizarGoogle, Long idUsuario) {
        acessoPaciente.exigirPropriedade(agendamento.getIdPaciente(), idUsuario);
        exigirHorarioLivre(agendamento, null);

        if (agendamento.getIdProfissional() == null && agendamento.getProfissional() != null) {

            Optional<Profissional> existente = profissionalRepository
                    .buscarPorNome(agendamento.getProfissional().getNomeProfissional());

            if (existente.isPresent()) {
                agendamento.setIdProfissional(existente.get().getId());
            } else {
                Profissional salvo = profissionalRepository.salvar(agendamento.getProfissional());
                agendamento.setIdProfissional(salvo.getId());
            }
        }
        agendamento.setProfissional(null);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setFavorito(false);
        agendamento.setArquivado(false);

        // Consulta é salva localmente primeiro: a sincronização com o Google é
        // opcional (RF06) e sua falha nunca pode impedir o agendamento local.
        Agendamento salvo = agendamentoRepository.salvar(agendamento);

        if (!sincronizarGoogle) {
            return ResultadoAgendamento.semAviso(salvo);
        }

        try {
            String accessToken = googleTokenUseCase.obterAccessTokenValido(idUsuario);
            String calendarId = googleTokenUseCase.obterCalendarioDoPaciente(salvo.getIdPaciente(), idUsuario);
            String googleEventId = googleCalendarRepository.criarEvento(accessToken, calendarId, salvo);
            salvo.setGoogleEventId(googleEventId);
            salvo = agendamentoRepository.atualizar(salvo);
            return ResultadoAgendamento.semAviso(salvo);
        } catch (RuntimeException e) {
            log.warn("Falha ao sincronizar agendamento {} com o Google Calendar: {}", salvo.getId(), e.getMessage());
            return new ResultadoAgendamento(salvo, "Consulta salva, mas não foi possível sincronizar com o Google Calendar: " + e.getMessage());
        }
    }

    /**
     * UC03 - Fluxo de Exceção: bloqueia o cadastro quando o paciente já tem consulta
     * no mesmo intervalo. Consultas canceladas não ocupam horário. Quando a hora de
     * término não foi informada, considera-se a duração padrão de uma hora.
     *
     * @param idIgnorado id do próprio agendamento em caso de reagendamento, para que
     *                   ele não conflite consigo mesmo.
     */
    private void exigirHorarioLivre(Agendamento novo, Long idIgnorado) {
        if (novo.getIdPaciente() == null || novo.getDataAgendamento() == null
                || novo.getHoraAgendamento() == null) {
            return;
        }

        boolean conflita = agendamentoRepository
                .buscarPorPacienteEData(novo.getIdPaciente(), novo.getDataAgendamento())
                .stream()
                .filter(existente -> !existente.getId().equals(idIgnorado))
                .filter(existente -> existente.getStatus() != StatusAgendamento.CANCELADO)
                .anyMatch(existente -> seSobrepoem(novo, existente));

        if (conflita) {
            throw new ConflitoAgendamentoException(
                    "Este paciente já possui uma consulta neste horário. Escolha outro horário para evitar choque de agendas.");
        }
    }

    private boolean seSobrepoem(Agendamento novo, Agendamento existente) {
        if (existente.getHoraAgendamento() == null) {
            return false;
        }

        LocalTime inicioNovo = novo.getHoraAgendamento();
        LocalTime fimNovo = fimDe(novo);
        LocalTime inicioExistente = existente.getHoraAgendamento();
        LocalTime fimExistente = fimDe(existente);

        return inicioNovo.isBefore(fimExistente) && inicioExistente.isBefore(fimNovo);
    }

    private boolean houveMudancaDeHorario(Agendamento atual, Agendamento dados) {
        return !Objects.equals(atual.getDataAgendamento(), dados.getDataAgendamento())
                || !Objects.equals(atual.getHoraAgendamento(), dados.getHoraAgendamento())
                || !Objects.equals(atual.getHoraFim(), dados.getHoraFim());
    }

    private LocalTime fimDe(Agendamento agendamento) {
        LocalTime fim = agendamento.getHoraFim();
        return fim != null && fim.isAfter(agendamento.getHoraAgendamento())
                ? fim
                : agendamento.getHoraAgendamento().plusHours(1);
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + id));
    }

    /** Mesma busca, confirmando antes que a consulta pertence a um perfil do usuário. */
    public Agendamento buscarDoUsuario(Long id, Long idUsuario) {
        Agendamento agendamento = buscarPorId(id);
        acessoPaciente.exigirPropriedade(agendamento.getIdPaciente(), idUsuario);
        return agendamento;
    }

    public List<Agendamento> listarAtivosPorPaciente(Long idPaciente, Long idUsuario) {
        acessoPaciente.exigirPropriedade(idPaciente, idUsuario);
        return agendamentoRepository.buscarAtivosPorPaciente(idPaciente);
    }

    public List<Agendamento> listarArquivadosPorPaciente(Long idPaciente, Long idUsuario) {
        acessoPaciente.exigirPropriedade(idPaciente, idUsuario);
        return agendamentoRepository.buscarArquivadosPorPaciente(idPaciente);
    }

    public ResultadoAgendamento atualizarStatus(Long id, StatusAgendamento novoStatus, Long idUsuario) {
        Agendamento agendamento = buscarDoUsuario(id, idUsuario);
        agendamento.alterarStatus(novoStatus);

        String aviso = null;

        // UC04: cancelamento remove o evento sincronizado, se existir. Falha no
        // Google não impede o cancelamento local.
        if (novoStatus == StatusAgendamento.CANCELADO && agendamento.getGoogleEventId() != null) {
            try {
                String accessToken = googleTokenUseCase.obterAccessTokenValido(idUsuario);
                String calendarId = googleTokenUseCase.obterCalendarioDoPaciente(agendamento.getIdPaciente(), idUsuario);
                googleCalendarRepository.removerEvento(accessToken, calendarId, agendamento.getGoogleEventId());
                agendamento.setGoogleEventId(null);
            } catch (RuntimeException e) {
                log.warn("Falha ao remover evento do Google Calendar para o agendamento {}: {}", id, e.getMessage());
                aviso = "Consulta cancelada, mas o evento não pôde ser removido do Google Calendar: " + e.getMessage();
            }
        }

        return new ResultadoAgendamento(agendamentoRepository.atualizar(agendamento), aviso);
    }

    public Agendamento arquivar(Long id, Long idUsuario) {
        Agendamento agendamento = buscarDoUsuario(id, idUsuario);
        agendamento.arquivar();
        return agendamentoRepository.atualizar(agendamento);
    }

    public Agendamento desarquivar(Long id, Long idUsuario) {
        Agendamento agendamento = buscarDoUsuario(id, idUsuario);
        agendamento.desarquivar();
        return agendamentoRepository.atualizar(agendamento);
    }

    public Agendamento toggleFavorito(Long id, Long idUsuario) {
        Agendamento agendamento = buscarDoUsuario(id, idUsuario);
        agendamento.toggleFavorito();
        return agendamentoRepository.atualizar(agendamento);
    }

    public ResultadoAgendamento atualizar(Long id, Agendamento dados, Long idUsuario) {
        Agendamento agendamento = buscarDoUsuario(id, idUsuario);

        // UC04 - Fluxo de Exceção: consulta finalizada não pode ser reagendada.
        if (agendamento.getStatus() == StatusAgendamento.FINALIZADO) {
            throw new IllegalStateException("Não é possível reagendar uma consulta finalizada.");
        }

        dados.setIdPaciente(agendamento.getIdPaciente());
        exigirHorarioLivre(dados, id);

        boolean mudouHorario = houveMudancaDeHorario(agendamento, dados);

        agendamento.setEspecialidade(dados.getEspecialidade());
        agendamento.setNomeClinica(dados.getNomeClinica());
        agendamento.setMotivoConsulta(dados.getMotivoConsulta());
        agendamento.setTipoConsulta(dados.getTipoConsulta());
        agendamento.setDataAgendamento(dados.getDataAgendamento());
        agendamento.setHoraAgendamento(dados.getHoraAgendamento());
        agendamento.setHoraFim(dados.getHoraFim());

        // RF05 - alterar data ou horário caracteriza reagendamento, e o status passa a
        // refletir isso. Consulta cancelada permanece cancelada.
        if (mudouHorario && agendamento.getStatus() != StatusAgendamento.CANCELADO) {
            agendamento.setStatus(StatusAgendamento.REAGENDADO);
        }

        String aviso = null;

        // UC04: reagendamento atualiza o evento já sincronizado, se existir.
        // Falha no Google não impede o reagendamento local.
        if (agendamento.getGoogleEventId() != null) {
            try {
                String accessToken = googleTokenUseCase.obterAccessTokenValido(idUsuario);
                String calendarId = googleTokenUseCase.obterCalendarioDoPaciente(agendamento.getIdPaciente(), idUsuario);
                googleCalendarRepository.atualizarEvento(accessToken, calendarId, agendamento.getGoogleEventId(), agendamento);
            } catch (RuntimeException e) {
                log.warn("Falha ao atualizar evento do Google Calendar para o agendamento {}: {}", id, e.getMessage());
                aviso = "Consulta reagendada, mas o evento no Google Calendar não pôde ser atualizado: " + e.getMessage();
            }
        }

        return new ResultadoAgendamento(agendamentoRepository.atualizar(agendamento), aviso);
    }

}
