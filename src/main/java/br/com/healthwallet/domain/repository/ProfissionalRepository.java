package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Profissional;

import java.util.List;
import java.util.Optional;

public interface ProfissionalRepository {
    List<Profissional> listarTodos();
    Optional<Profissional> buscarPorId(Long id);
    Optional<Profissional> buscarPorNome(String nomeProfissional);
    Profissional salvar(Profissional profissional);
    void deletar(Long id);
}
