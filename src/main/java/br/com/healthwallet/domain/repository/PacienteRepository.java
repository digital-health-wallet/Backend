package br.com.healthwallet.domain.repository;

import br.com.healthwallet.domain.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository {
    List<Paciente> listarTodos();
    Optional<Paciente> buscarPorId(Long id);
    List<Paciente> buscarPorUsuario(Long idUsuario);
    Paciente salvar(Paciente paciente);
    void deletar(Long id);
}
