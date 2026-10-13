package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Exame;
import java.util.List;
import java.util.Optional;

public interface ExameRepository {
    Exame salvar(Exame exame);
    Optional<Exame> buscarPorId(Long id);
    List<Exame> buscarPorAgendamento(Long idAgendamento);
    List<Exame> buscarAvulsos();
    List<Exame> buscarPorPaciente(Long idPaciente);
    void desativar(Long id);
}