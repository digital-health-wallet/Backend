package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    Usuario salvar(Usuario usuario);
}
