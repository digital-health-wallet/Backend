package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Diagnostico;
import java.util.List;
import java.util.Optional;

public interface DiagnosticoRepository {
    Diagnostico salvar(Diagnostico diagnostico);
    Optional<Diagnostico> buscarPorId(Long id);
    List<Diagnostico> buscarPorAgendamento(Long idAgendamento);
    void deletar(Long id);
}