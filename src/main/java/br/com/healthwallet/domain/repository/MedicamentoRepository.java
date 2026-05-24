package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Medicamento;
import java.util.Optional;

public interface MedicamentoRepository {
    Medicamento salvar(Medicamento medicamento);
    Optional<Medicamento> buscarPorNome(String nome);
}