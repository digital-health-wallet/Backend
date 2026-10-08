package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * RNF02 - Antes de retornar ou modificar dados de um paciente, confirma que o
 * perfil pertence ao usuário autenticado. Sem esta verificação bastaria trocar o
 * identificador na URL para alcançar o histórico clínico de outra conta.
 */
@Component
@RequiredArgsConstructor
public class AcessoPaciente {

    private final PacienteRepository pacienteRepository;
    private final AgendamentoRepository agendamentoRepository;

    public void exigirPropriedade(Long idPaciente, Long idUsuario) {
        if (idPaciente == null) {
            throw new SecurityException("Acesso negado.");
        }

        Paciente paciente = pacienteRepository.buscarPorId(idPaciente)
                .orElseThrow(() -> new SecurityException("Acesso negado."));

        if (!paciente.getIdUsuario().equals(idUsuario)) {
            throw new SecurityException("Acesso negado.");
        }
    }

    /**
     * Mesma verificação a partir de uma consulta: o documento pertence ao paciente
     * da consulta, e a consulta precisa ser de um perfil do usuário autenticado.
     */
    public void exigirPropriedadePorAgendamento(Long idAgendamento, Long idUsuario) {
        Long idPaciente = agendamentoRepository.buscarPorId(idAgendamento)
                .map(a -> a.getIdPaciente())
                .orElseThrow(() -> new SecurityException("Acesso negado."));

        exigirPropriedade(idPaciente, idUsuario);
    }
}
