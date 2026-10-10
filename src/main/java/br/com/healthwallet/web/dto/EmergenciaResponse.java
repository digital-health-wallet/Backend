package br.com.healthwallet.web.dto;

import br.com.healthwallet.application.usecase.FichaEmergencial;
import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.model.ItemReceita;

import java.util.List;

/**
 * UC06 - Acessar Ficha via QR Code. Exibe estritamente dados vitais
 * de emergência (RF13): tipo sanguíneo, alergias, diagnósticos crônicos
 * e medicamentos de uso contínuo. Nenhum outro dado do paciente é exposto.
 */
public record EmergenciaResponse(
        String nome,
        String tipoSanguineo,
        List<AlergiaResumo> alergias,
        List<DiagnosticoResumo> diagnosticosCronicos,
        List<MedicamentoResumo> medicamentosUsoContinuo
) {
    public record AlergiaResumo(String tipo, String descricao) {
    }

    public record DiagnosticoResumo(String nome, String cid, String descricao) {
    }

    public record MedicamentoResumo(String nomeMedicamento, String posologia) {
    }

    public static EmergenciaResponse from(FichaEmergencial ficha) {
        if (ficha == null) return null;

        return new EmergenciaResponse(
                ficha.paciente().getNome(),
                ficha.paciente().getTipoSanguineo(),
                ficha.alergias().stream().map(EmergenciaResponse::toResumo).toList(),
                ficha.diagnosticosCronicos().stream().map(EmergenciaResponse::toResumo).toList(),
                ficha.medicamentosUsoContinuo().stream().map(EmergenciaResponse::toResumo).toList());
    }

    private static AlergiaResumo toResumo(Alergia alergia) {
        return new AlergiaResumo(
                alergia.getTipo() != null ? alergia.getTipo().name() : null,
                alergia.getDescricao());
    }

    private static DiagnosticoResumo toResumo(Diagnostico diagnostico) {
        return new DiagnosticoResumo(diagnostico.getNome(), diagnostico.getCid(), diagnostico.getDescricao());
    }

    private static MedicamentoResumo toResumo(ItemReceita item) {
        String nome = item.getMedicamento() != null ? item.getMedicamento().getNomeMedicamento() : null;
        return new MedicamentoResumo(nome, item.getPosologia());
    }
}
