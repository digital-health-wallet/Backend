package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Receita;
import java.util.List;
import java.util.Optional;

public interface ReceitaRepository {
    Receita salvar(Receita receita);
    Optional<Receita> buscarPorId(Long id);
    List<Receita> buscarPorAgendamento(Long idAgendamento);
    List<Receita> buscarAvulsas();
    List<ItemReceita> buscarItensUsoContinuoPorPaciente(Long idPaciente);
    void deletar(Long id);
}