package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Agendamento;

import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository {
    Agendamento salvar(Agendamento agendamento);
    Optional<Agendamento> buscarPorId(Long id);
    List<Agendamento> buscarPorPaciente(Long idPaciente);
    List<Agendamento> buscarAtivosPorPaciente(Long idPaciente);
    List<Agendamento> buscarArquivadosPorPaciente(Long idPaciente);
    Agendamento atualizar(Agendamento agendamento);
    void deletar(Long id);
}
