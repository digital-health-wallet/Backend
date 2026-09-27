package br.com.healthwallet.web.dto;

import br.com.healthwallet.application.usecase.PacienteComAlergia;
import br.com.healthwallet.domain.model.enums.TipoAlergia;

import java.time.LocalDate;
import java.util.List;

public record PacienteResponse(
        Long id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String tipoSanguineo,
        Boolean fichaEmergencialAtiva,
        Boolean ativo,
        String codigoEmergencia,
        Boolean possuiAlergia,
        List<AlergiaResumo> alergias,
        List<EmergenciaResponse.DiagnosticoResumo> diagnosticosCronicos,
        List<EmergenciaResponse.MedicamentoResumo> medicamentosUsoContinuo
) {
    public record AlergiaResumo(Long id, TipoAlergia tipo, String descricao) {
    }

    public static PacienteResponse from(PacienteComAlergia dados) {
        List<AlergiaResumo> alergias = dados.alergias().stream()
                .map(a -> new AlergiaResumo(a.getId(), a.getTipo(), a.getDescricao()))
                .toList();
        return new PacienteResponse(
                dados.paciente().getId(),
                dados.paciente().getNome(),
                dados.paciente().getCpf(),
                dados.paciente().getDataNascimento(),
                dados.paciente().getTipoSanguineo(),
                dados.paciente().getFichaEmergencialAtiva(),
                dados.paciente().getAtivo(),
                dados.paciente().getCodigoEmergencia(),
                !alergias.isEmpty(),
                alergias,
                dados.diagnosticosCronicos().stream()
                        .map(d -> new EmergenciaResponse.DiagnosticoResumo(d.getNome(), d.getCid(), d.getDescricao()))
                        .toList(),
                dados.medicamentosUsoContinuo().stream()
                        .map(i -> new EmergenciaResponse.MedicamentoResumo(
                                i.getMedicamento() != null ? i.getMedicamento().getNomeMedicamento() : null,
                                i.getPosologia()))
                        .toList()
        );
    }
}
