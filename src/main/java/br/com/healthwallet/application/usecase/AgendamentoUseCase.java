package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;

    public Agendamento criar(Agendamento agendamento){
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
