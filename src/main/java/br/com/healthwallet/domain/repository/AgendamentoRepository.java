package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Agendamento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository {
    Agendamento salvar(Agendamento agendamento);
    Optional<Agendamento> buscarPorId(Long id);
    List<Agendamento> buscarPorPaciente(Long idPaciente);
    List<Agendamento> buscarAtivosPorPaciente(Long idPaciente);
    List<Agendamento> buscarArquivadosPorPaciente(Long idPaciente);
    List<Agendamento> buscarPorPacienteEData(Long idPaciente, LocalDate data);
    Agendamento atualizar(Agendamento agendamento);
}
