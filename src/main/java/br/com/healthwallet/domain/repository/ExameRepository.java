package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Exame;
import java.util.List;
import java.util.Optional;

public interface ExameRepository {
    Exame salvar(Exame exame);
    Optional<Exame> buscarPorId(Long id);
    List<Exame> buscarPorAgendamento(Long idAgendamento); // Busca os vinculados a consultas
    List<Exame> buscarAvulsos(); // Busca os que têm idAgendamento = null
    List<Exame> buscarPorPaciente(Long idPaciente); // Vinculados às consultas do paciente + avulsos
    void deletar(Long id);
    void desativar(Long id);
}