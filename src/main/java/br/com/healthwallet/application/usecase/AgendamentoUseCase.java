package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.Profissional;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;

    public Agendamento criar(Agendamento agendamento) {
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
        return agendamentoRepository.salvar(agendamento);
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

    public Agendamento atualizarStatus(Long id, StatusAgendamento novoStatus) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.alterarStatus(novoStatus);
        return agendamentoRepository.atualizar(agendamento);
    }

    public Agendamento arquivar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.arquivar();
        return agendamentoRepository.atualizar(agendamento);
    }

    public Agendamento toggleFavorito(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.toggleFavorito();
        return agendamentoRepository.atualizar(agendamento);
    }

    public Agendamento atualizar(Long id, Agendamento dados) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setEspecialidade(dados.getEspecialidade());
        agendamento.setNomeClinica(dados.getNomeClinica());
        agendamento.setMotivoConsulta(dados.getMotivoConsulta());
        agendamento.setTipoConsulta(dados.getTipoConsulta());
        agendamento.setDataAgendamento(dados.getDataAgendamento());
        agendamento.setHoraAgendamento(dados.getHoraAgendamento());
        agendamento.setHoraFim(dados.getHoraFim());
        return agendamentoRepository.atualizar(agendamento);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        agendamentoRepository.deletar(id);
    }
}
