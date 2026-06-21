package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Alergia;

import java.util.List;
import java.util.Optional;

public interface AlergiaRepository {
    List<Alergia> listarTodos();
    Optional<Alergia> buscarPorId(Long id);
    List<Alergia> buscarPorPaciente(Long idPaciente);
    Alergia salvar(Alergia alergia);
    void deletar(Long id);
}
