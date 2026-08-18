package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.Profissional;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.GoogleCalendarRepository;
import br.com.healthwallet.domain.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final GoogleCalendarRepository googleCalendarRepository;
    private final GoogleTokenUseCase googleTokenUseCase;

    public ResultadoAgendamento criar(Agendamento agendamento, boolean sincronizarGoogle, Long idUsuario) {
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

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + id));
    }

    public List<Agendamento> listarAtivosPorPaciente(Long idPaciente) {
        return agendamentoRepository.buscarAtivosPorPaciente(idPaciente);
    }

    public List<Agendamento> listarArquivadosPorPaciente(Long idPaciente) {
        return agendamentoRepository.buscarArquivadosPorPaciente(idPaciente);
    }

    public ResultadoAgendamento atualizarStatus(Long id, StatusAgendamento novoStatus, Long idUsuario) {
        Agendamento agendamento = buscarPorId(id);
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

    public Agendamento arquivar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.arquivar();
        return agendamentoRepository.atualizar(agendamento);
    }

    public Agendamento desarquivar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.desarquivar();
        return agendamentoRepository.atualizar(agendamento);
    }

    public Agendamento toggleFavorito(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.toggleFavorito();
        return agendamentoRepository.atualizar(agendamento);
    }

    public ResultadoAgendamento atualizar(Long id, Agendamento dados, Long idUsuario) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setEspecialidade(dados.getEspecialidade());
        agendamento.setNomeClinica(dados.getNomeClinica());
        agendamento.setMotivoConsulta(dados.getMotivoConsulta());
        agendamento.setTipoConsulta(dados.getTipoConsulta());
        agendamento.setDataAgendamento(dados.getDataAgendamento());
        agendamento.setHoraAgendamento(dados.getHoraAgendamento());
        agendamento.setHoraFim(dados.getHoraFim());

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

    public void deletar(Long id) {
        buscarPorId(id);
        agendamentoRepository.deletar(id);
    }
}
