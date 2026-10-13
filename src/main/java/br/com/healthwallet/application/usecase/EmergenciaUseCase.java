package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmergenciaUseCase {

    private final PacienteRepository pacienteRepository;
    private final AlergiaRepository alergiaRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final ReceitaRepository receitaRepository;

    public FichaEmergencial buscarFichaPublica(String codigoEmergencia) {
        Paciente paciente = pacienteRepository.buscarPorCodigoEmergencia(codigoEmergencia)
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                .orElseThrow(() -> new SecurityException("Acesso negado."));

        if (!Boolean.TRUE.equals(paciente.getFichaEmergencialAtiva())) {
            throw new SecurityException("Acesso negado.");
        }

        return new FichaEmergencial(
                paciente,
                alergiaRepository.buscarPorPaciente(paciente.getId()),
                diagnosticoRepository.buscarCronicosPorPaciente(paciente.getId()),
                receitaRepository.buscarItensUsoContinuoPorPaciente(paciente.getId()));
    }
}
