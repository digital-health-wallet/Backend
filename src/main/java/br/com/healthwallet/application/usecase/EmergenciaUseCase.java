package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import br.com.healthwallet.web.dto.EmergenciaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UC06 - Acessar Ficha via QR Code (Módulo de Emergência). Rota pública
 * (RF13), acessível por socorristas não autenticados, restrita a dados
 * vitais e apenas quando o Paciente ativou a ficha de emergência.
 */
@Service
@RequiredArgsConstructor
public class EmergenciaUseCase {

    private final PacienteRepository pacienteRepository;
    private final AlergiaRepository alergiaRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final ReceitaRepository receitaRepository;

    public EmergenciaResponse buscarFichaPublica(String codigoEmergencia) {
        Paciente paciente = pacienteRepository.buscarPorCodigoEmergencia(codigoEmergencia)
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                .orElseThrow(() -> new SecurityException("Acesso negado."));

        if (!Boolean.TRUE.equals(paciente.getFichaEmergencialAtiva())) {
            throw new SecurityException("Acesso negado.");
        }

        List<EmergenciaResponse.AlergiaResumo> alergias = alergiaRepository.buscarPorPaciente(paciente.getId())
                .stream()
                .map(this::toResumo)
                .toList();

        List<EmergenciaResponse.DiagnosticoResumo> diagnosticos = diagnosticoRepository
                .buscarCronicosPorPaciente(paciente.getId())
                .stream()
                .map(this::toResumo)
                .toList();

        List<EmergenciaResponse.MedicamentoResumo> medicamentos = receitaRepository
                .buscarItensUsoContinuoPorPaciente(paciente.getId())
                .stream()
                .map(this::toResumo)
                .toList();

        return new EmergenciaResponse(paciente.getNome(), paciente.getTipoSanguineo(), alergias, diagnosticos, medicamentos);
    }

    private EmergenciaResponse.AlergiaResumo toResumo(Alergia alergia) {
        return new EmergenciaResponse.AlergiaResumo(
                alergia.getTipo() != null ? alergia.getTipo().name() : null,
                alergia.getDescricao());
    }

    private EmergenciaResponse.DiagnosticoResumo toResumo(Diagnostico diagnostico) {
        return new EmergenciaResponse.DiagnosticoResumo(diagnostico.getNome(), diagnostico.getCid(), diagnostico.getDescricao());
    }

    private EmergenciaResponse.MedicamentoResumo toResumo(ItemReceita item) {
        String nome = item.getMedicamento() != null ? item.getMedicamento().getNomeMedicamento() : null;
        return new EmergenciaResponse.MedicamentoResumo(nome, item.getPosologia());
    }
}
