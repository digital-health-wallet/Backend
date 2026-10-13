package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


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

    public void exigirPropriedadePorAgendamento(Long idAgendamento, Long idUsuario) {
        Long idPaciente = agendamentoRepository.buscarPorId(idAgendamento)
                .map(a -> a.getIdPaciente())
                .orElseThrow(() -> new SecurityException("Acesso negado."));

        exigirPropriedade(idPaciente, idUsuario);
    }
}
