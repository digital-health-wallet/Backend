package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Contato;

import java.util.List;
import java.util.Optional;

public interface ContatoRepository {
    List<Contato> listarTodos();
    Optional<Contato> buscarPorId(Long id);
    Contato salvar(Contato contato);
    void deletar(Long id);
}
